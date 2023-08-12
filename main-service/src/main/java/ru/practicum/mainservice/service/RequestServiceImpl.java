package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateResponseDto;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.exception.DataException;
import ru.practicum.mainservice.mapper.RequestMapper;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.Request;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.model.enums.EventState;
import ru.practicum.mainservice.model.enums.RequestStatus;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.interfaces.RequestService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.service.UtilityClass.*;

@AllArgsConstructor
@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        Optional<Request> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            throw new DataException(REQUEST_NOT_FOUND);
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(USER_NOT_FOUND)
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(EVENT_NOT_FOUND)
        );
        if (userId.equals(event.getInitiator().getId())) {
            throw new DataException("Нельзя отправить запрос к своему же событию");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new DataException("Событие не опубликовано");
        }
        int limit = event.getParticipantLimit();
        if (limit != 0) {
            Long numberOfRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (numberOfRequests >= limit) {
                throw new DataException("В событии уже максимальное кол-во участников");
            }
        }
        RequestStatus requestStatus = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requestStatus = RequestStatus.CONFIRMED;
        }
        Request newRequest = new Request(event, user, requestStatus);
        return RequestMapper.INSTANCE.toDto(requestRepository.save(newRequest));
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND));
        if (request.getRequester().getId().equals(userId)) {
            request.setStatus(RequestStatus.CANCELED);
            return RequestMapper.INSTANCE.toDto(requestRepository.save(request));
        }
        throw new DataException("Нельзя отменять чужие запросы");
    }

    @Transactional(readOnly = true)
    public List<RequestDto> getAllUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND);
        }
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RequestDto> getAllUserEventRequests(Long eventId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND);
        }
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(EVENT_NOT_FOUND)
        );
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataException("Пользователь не может просматривать запросы к событию, автором которого он не является");
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResponseDto updateRequestsStatus(
            EventRequestStatusUpdateRequestDto updater,
            Long eventId,
            Long userId
    ) {
        RequestStatus status = updater.getStatus();
        if (status == RequestStatus.CONFIRMED || status == RequestStatus.REJECTED) {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException(USER_NOT_FOUND);
            }
            Event event = eventRepository.findById(eventId).orElseThrow(
                    () -> new EntityNotFoundException(EVENT_NOT_FOUND)
            );
            if (!event.getInitiator().getId().equals(userId)) {
                throw new DataException("Пользователь не может обновлять запросы к событию, автором которого он не является");
            }
            Integer participantLimit = event.getParticipantLimit();
            if (!event.getRequestModeration() || participantLimit == 0) {
                throw new DataException("Событию не нужна модерация");
            }
            Long numberOfParticipants = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (numberOfParticipants >= participantLimit) {
                throw new DataException("В событии уже максимальное кол-во участников");
            }
            List<Request> requests = requestRepository.findAllByIdIn(updater.getRequestIds());
            RequestStatus newStatus = updater.getStatus();
            for (Request request : requests) {
                if (request.getEvent().getId().equals(eventId)) {
                    if (participantLimit > numberOfParticipants) {
                        if (newStatus == RequestStatus.CONFIRMED && request.getStatus() != RequestStatus.CONFIRMED) {
                            numberOfParticipants++;
                        }
                        request.setStatus(newStatus);
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                    }
                } else {
                    throw new DataException("Запрос и событие не совпадают");
                }
            }
            requests = requestRepository.saveAll(requests);
            List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            List<Request> rejectedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.REJECTED);

            List<RequestDto> confirmedRequestDtos = confirmedRequests.stream()
                    .map(RequestMapper.INSTANCE::toDto)
                    .collect(Collectors.toList());

            List<RequestDto> rejectedRequestDtos = rejectedRequests.stream()
                    .map(RequestMapper.INSTANCE::toDto)
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResponseDto(confirmedRequestDtos, rejectedRequestDtos);
        } else {
            throw new IllegalArgumentException("Доступны только статусы CONFIRMED или REJECTED");
        }
    }
}
