package ru.practicum.mainservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.NewEventDto;
import ru.practicum.mainservice.dto.event.UpdateEventUserRequestDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.mainservice.dto.request.ParticipationRequestDto;
import ru.practicum.mainservice.enums.RequestStatus;
import ru.practicum.mainservice.exception.DataConflictException;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.service.RequestService;
import ru.practicum.mainservice.util.PageParams;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable("userId") final Long userId,
            @RequestParam("eventId") final Long eventId
    ) {
        log.info("Create request for eventId: {}, userId: {}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable("userId") final Long userId,
            @PathVariable("requestId") final Long requestId
    ) {
        log.info("Cancel request for requestId: {}, userId: {}", requestId, userId);
        return requestService.cancelOwnRequest(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getAllUserRequests(
            @PathVariable("userId") final Long userId
    ) {
        log.info("Get all requests for userId: {}", userId);
        return requestService.getAllUserRequests(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllUserEventRequests(
            @PathVariable("userId") final Long userId,
            @PathVariable("eventId") final Long eventId
    ) {
        log.info("Get all requests for eventId: {} by userId: {}", eventId, userId);
        return requestService.getAllUserEventRequests(eventId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateRequestsStatus(
            @Valid
            @RequestBody
            EventRequestStatusUpdateRequestDto updater,
            @PathVariable("userId") final Long userId,
            @PathVariable("eventId") final Long eventId
    ) {
        RequestStatus status = updater.getStatus();
        if (status == RequestStatus.CONFIRMED || status == RequestStatus.REJECTED) {
            log.info("Update eventId: {}, userId: {}", eventId, userId);
            return requestService.updateRequestsStatus(updater, eventId, userId);
        } else {
            log.warn("Update eventId: {}, userId: {}, status: {}", eventId, userId, status);
            throw new IllegalArgumentException("Only status CONFIRMED and REJECTED are allowed");
        }
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable("userId") final Long userId,
            @Valid
            @RequestBody
            NewEventDto eventDto
    ) {
        LocalDateTime eventDate = eventDto.getEventDate();
        LocalDateTime timeCriteria = LocalDateTime.now().plusHours(2L);
        if (eventDate.isBefore(timeCriteria)) {
            log.warn("Creating event by userId: {} fault", userId);
            throw new DataConflictException("Event date must be at least 2 hours from now");
        }
        log.info("Creating event by userId: {}", userId);
        return eventService.createEvent(userId, eventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable("userId") final Long userId,
            @PathVariable("eventId") final Long eventId,
            @Valid
            @RequestBody
            UpdateEventUserRequestDto eventDto
    ) {
        LocalDateTime eventDate = eventDto.getEventDate();
        LocalDateTime timeCriteria = LocalDateTime.now().plusHours(2L);
        if (eventDate != null && eventDate.isBefore(timeCriteria)) {
            log.warn("Updating event by userId: {} fault", userId);
            throw new DataConflictException("Event date must be at least 2 hours from now");
        }
        log.info("Updating event by userId: {}", userId);
        return eventService.updateEventByUser(eventDto, eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByIdByInitiator(
            @PathVariable("userId") final Long userId,
            @PathVariable("eventId") final Long eventId
    ) {
        log.info("Get event info by userId: {}, eventId: {}", userId, eventId);
        return eventService.getEventByIdByInitiator(eventId, userId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsInitiatedByUser(
            @PathVariable("userId")
            final Long userId,
            final PageParams pageParams
    ) {
        log.info("Get events by userId: {}, pageParams: {}", userId, pageParams);
        return eventService.getEventsInitiatedByUser(userId, pageParams);
    }
}
