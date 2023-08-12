package ru.practicum.mainservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.service.interfaces.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.mainservice.service.UtilityClass.PATTERN;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/events")
public class EventController {
    private final EventService eventService;

    @GetMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEvent(
            @PathVariable("eventId") Long eventId,
            HttpServletRequest request
    ) {
        log.info("Вызов события с id={}", eventId);
        return eventService.getEventByIdPublic(eventId, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(
            HttpServletRequest request,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort
    ) {
        log.info("Вызов всех событий");
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                request.getRemoteAddr(), request.getRequestURI(), from, size);
    }
}
