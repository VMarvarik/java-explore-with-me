package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.mainservice.dto.request.ParticipationRequestDto;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.Request;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.enums.EventState;
import ru.practicum.mainservice.enums.RequestStatus;
import ru.practicum.mainservice.exception.DataConflictException;
import ru.practicum.mainservice.mapper.RequestMapper;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Checking if request from user with id={} to event with id={} already exists", userId, eventId);
        Optional<Request> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            log.warn("Request from user with id={} to event with id={} already exists", userId, eventId);
            throw new DataConflictException("Request already exists");
        }
        log.info("Getting user with id={} from DB", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User with id={} was not found", userId);
                    return new EntityNotFoundException(String.format("User with id=%s was not found", userId));
                }
        );
        log.info("Getting event with id={} from DB", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Event with id={} was not found", eventId);
                    return new EntityNotFoundException(String.format("Event with id=%s was not found", eventId));
                }
        );
        if (userId.equals(event.getInitiator().getId())) {
            log.warn("User trying to send participation request to his own events");
            throw new DataConflictException("User can't sen participation request to his own events");
        }
        if (event.getState() != EventState.PUBLISHED) {
            log.warn("Trying to send request to event which is not published");
            throw new DataConflictException("Event unpublished");
        }
        log.info("Checking if event with id={} has participant limit", eventId);
        int limit = event.getParticipantLimit();
        if (limit != 0) {
            log.info("Counting confirmed requests for event with id={}", eventId);
            Long numberOfRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (numberOfRequests >= limit) {
                log.warn("Event with id={} participant limit already reached", eventId);
                throw new DataConflictException("Event is full of participants");
            }
        }
        log.info("Setting default RequestStatus for new request (PENDING)");
        RequestStatus requestStatus = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.info("Event with id={} doesn't need moderation or has no participant limit. Setting RequestStatus to CONFIRMED", eventId);
            requestStatus = RequestStatus.CONFIRMED;
        }
        Request newRequest = new Request(event, user, requestStatus);
        log.info("Saving new request to DB");
        return RequestMapper.INSTANCE.toDto(requestRepository.save(newRequest));
    }

    @Transactional
    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        log.info("Getting request with id={} from DB", requestId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> {
                    log.warn("Request with id={} was not found", requestId);
                    return new EntityNotFoundException(String.format("Request with id=%s was not found", requestId));
                }
        );
        log.info("Checking if user with id={} is requester of request with id={}", userId, requestId);
        if (request.getRequester().getId().equals(userId)) {
            log.info("Setting RequestStatus to CANCELED");
            request.setStatus(RequestStatus.CANCELED);
            log.info("Updating request with id={} to DB", requestId);
            return RequestMapper.INSTANCE.toDto(requestRepository.save(request));
        }
        log.warn("User with id={} trying to cancel request with id={} which he is not requester of", userId, requestId);
        throw new DataConflictException("User can't cancel other users requests");
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) {
        log.info("Checking if user with id={} exists in DB", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found", userId);
            throw new EntityNotFoundException(String.format("User with id=%s was not found", userId));
        }
        log.info("Getting requests from DB for user with id={}", userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Mapping requests to DTO");
        return requests.stream()
                .map(RequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllUserEventRequests(Long eventId, Long userId) {
        log.info("Checking if user with id={} exists in DB", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found", userId);
            throw new EntityNotFoundException(String.format("User with id=%s was not found", userId));
        }
        log.info("Getting event with id={} from DB", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Event with id={} was not found", eventId);
                    return new EntityNotFoundException(String.format("Event with id=%s was not found", eventId));
                }
        );
        log.info("Checking if user with id={} is initiator of event with id={}", userId, eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("User with id={} trying to see other users' requests in event with id={} where he is not the initiator", userId, eventId);
            throw new DataConflictException("User can't see other users' requests in events where he is not the initiator");
        }
        log.info("Getting requests from DB for event with id={}", eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.info("Mapping requests to DTO");
        return requests.stream()
                .map(RequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResultDto updateRequestsStatus(
            EventRequestStatusUpdateRequestDto updater,
            Long eventId,
            Long userId
    ) {
        log.info("Checking if user with id={} exists in DB", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found", userId);
            throw new EntityNotFoundException(String.format("User with id=%s was not found", userId));
        }
        log.info("Getting event with id={} from DB", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Event with id={} was not found", eventId);
                    return new EntityNotFoundException(String.format("Event with id=%s was not found", eventId));
                }
        );
        log.info("Checking if user with id={} is initiator of event with id={}", userId, eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("User with id={} trying to update other users' requests in event with id={} where he is not the initiator", userId, eventId);
            throw new DataConflictException("User can't update other users' requests in events where he is not initiator");
        }
        log.info("Checking if event requires moderation");
        Integer participantLimit = event.getParticipantLimit();
        if (!event.getRequestModeration() || participantLimit == 0) {
            log.warn("Trying moderate requests in event with id={} which doesn't require moderation", eventId);
            throw new DataConflictException("Event doesn't require moderation");
        }
        log.info("Counting confirmed requests in event");
        Long numberOfParticipants = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        log.info("Checking if event is already full of participants");
        if (numberOfParticipants >= participantLimit) {
            log.warn("Trying moderate requests in event with id={} which is already full of participants", eventId);
            throw new DataConflictException("Event is already full of participants");
        }
        log.info("Getting requests from DB for event with id={} to update statuses", eventId);
        List<Request> requests = requestRepository.findAllByIdIn(updater.getRequestIds());

        RequestStatus newStatus = updater.getStatus();
        for (Request request : requests) {
            if (request.getEvent().getId().equals(eventId)) {
                if (participantLimit > numberOfParticipants) {
                    if (newStatus == RequestStatus.CONFIRMED && request.getStatus() != RequestStatus.CONFIRMED) {
                        numberOfParticipants++;
                    }
                    log.info("Setting new status={} to request with id={}", newStatus, request.getId());
                    request.setStatus(newStatus);
                } else {
                    log.info("Setting status REJECTED to request with id={}", request.getId());
                    request.setStatus(RequestStatus.REJECTED);
                }
            } else {
                log.warn("Trying update request with id={} which is not related to event with id={}",
                        request.getId(), event.getId());
                throw new DataConflictException(
                        String.format("Request with id=%s can't be updated. Event with id=%s is not related to it. " +
                                        "Nothing was updated",
                                request.getId(), event.getId()
                        )
                );
            }
        }

        log.info("Saving updated requests to DB");
        requestRepository.saveAll(requests);
        log.info("Getting all requests for event with id={} from DB  to return", eventId);
        requests = requestRepository.findAllByEventId(eventId);
        List<ParticipationRequestDto> confirmedRequests = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.CONFIRMED)
                .map(RequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.REJECTED)
                .map(RequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResultDto(confirmedRequests, rejectedRequests);
    }
}
