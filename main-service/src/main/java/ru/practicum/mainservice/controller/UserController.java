package ru.practicum.mainservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.EventUpdateRequestDto;
import ru.practicum.mainservice.dto.event.NewEventDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateResponseDto;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.service.interfaces.EventService;
import ru.practicum.mainservice.service.interfaces.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
            @PathVariable("userId") Long userId,
            @RequestParam("eventId") Long eventId
    ) {
        log.info("Создание запроса с eventId: {}, с userId: {}", eventId, userId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(
            @PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId
    ) {
        log.info("Отмена запроса с requestId: {}, с userId: {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getAllUserRequests(
            @PathVariable("userId") Long userId
    ) {
        log.info("Вызов всех запросов для userId: {}", userId);
        return requestService.getAllUserRequests(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getAllUserEventRequests(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId
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
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Обновление события с eventId: {}, для userId: {}", eventId, userId);
        return requestService.updateRequestsStatus(updater, eventId, userId);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(
            @PathVariable("userId") Long userId,
            @Valid
            @RequestBody
            NewEventDto eventDto
    ) {
        log.info("Создание события пользователем с userId: {}", userId);
        return eventService.addEvent(userId, eventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEventByUser(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid
            @RequestBody
            EventUpdateRequestDto eventDto
    ) {
        log.info("Обновление события пользователем с userId: {}", userId);
        return eventService.updateEventByUser(eventDto, eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventByIdByInitiator(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Получение события с userId: {}, с eventId: {}", userId, eventId);
        return eventService.getEventByIdByInitiator(eventId, userId);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsInitiatedByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size
    ) {
        log.info("Получение событий пользователя");
        return eventService.getEventsInitiatedByUser(userId, from, size);
    }
}
