package ru.practicum.mainservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventDto;
import ru.practicum.mainservice.dto.event.EventUpdateRequestDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.NewEventDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateResponseDto;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.exception.DataException;
import ru.practicum.mainservice.model.enums.RequestStatus;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public RequestDto addRequest(
            @PathVariable("userId")
            final Long userId,
            @RequestParam("eventId")
            final Long eventId
    ) {
        log.info("Создание запроса с eventId: {}, с userId: {}", eventId, userId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(
            @PathVariable("userId")
            final Long userId,
            @PathVariable("requestId")
            final Long requestId
    ) {
        log.info("Отмена запроса с requestId: {}, с userId: {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getAllUserRequests(
            @PathVariable("userId")
            final Long userId
    ) {
        log.info("Вызов всех запросов для userId: {}", userId);
        return requestService.getAllUserRequests(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getAllUserEventRequests(
            @PathVariable("userId")
            final Long userId,
            @PathVariable("eventId")
            final Long eventId
    ) {
        log.info("Вызов всех запросов с eventId: {} с userId: {}", eventId, userId);
        return requestService.getAllUserEventRequests(eventId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResponseDto updateRequestsStatus(
            @Valid
            @RequestBody
            EventRequestStatusUpdateRequestDto updater,
            @PathVariable("userId")
            final Long userId,
            @PathVariable("eventId")
            final Long eventId
    ) {
        RequestStatus status = updater.getStatus();
        if (status == RequestStatus.CONFIRMED || status == RequestStatus.REJECTED) {
            log.info("Обновление события с eventId: {}, для userId: {}", eventId, userId);
            return requestService.updateRequestsStatus(updater, eventId, userId);
        } else {
            throw new IllegalArgumentException("Доступны только статусы CONFIRMED или REJECTED");
        }
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(
            @PathVariable("userId")
            final Long userId,
            @Valid
            @RequestBody
            NewEventDto eventDto
    ) {
        LocalDateTime eventDate = eventDto.getEventDate();
        LocalDateTime timeCriteria = LocalDateTime.now().plusHours(2L);
        if (eventDate.isBefore(timeCriteria)) {
            throw new DataException("Время события не может таким ранним");
        }
        log.info("Создание события пользователем с userId: {}", userId);
        return eventService.createEvent(userId, eventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto addEvent(
            @PathVariable("userId")
            final Long userId,
            @PathVariable("eventId")
            final Long eventId,
            @Valid
            @RequestBody
            EventUpdateRequestDto eventDto
    ) {
        LocalDateTime eventDate = eventDto.getEventDate();
        LocalDateTime timeCriteria = LocalDateTime.now().plusHours(2L);
        if (eventDate != null && eventDate.isBefore(timeCriteria)) {
            throw new DataException("Время события не может таким ранним");
        }
        log.info("Обновление события пользователем с userId: {}", userId);
        return eventService.updateEventByUser(eventDto, eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventByIdByInitiator(
            @PathVariable("userId")
            final Long userId,
            @PathVariable("eventId")
            final Long eventId
    ) {
        log.info("Получение события с userId: {}, с eventId: {}", userId, eventId);
        return eventService.getEventByIdByInitiator(eventId, userId);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsInitiatedByUser(
            @PathVariable("userId")
            final Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size
    ) {
        log.info("Получение событий пользователя");
        return eventService.getEventsInitiatedByUser(userId, from, size);
    }
}
