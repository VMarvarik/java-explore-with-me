package ru.practicum.mainservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.enums.EventSort;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.util.PageParams;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/events")
public class EventController {
    private EventService eventService;

    @GetMapping(path = "/{eventId}")
    public EventFullDto getEvent(
            @PathVariable("eventId") final Long eventId,
            HttpServletRequest request
    ) {
        log.info("Get event by id={}", eventId);
        return eventService.getEventByIdPublic(eventId, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            HttpServletRequest request,
            PageParams params,
            @RequestParam(required = false) final String text,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeEnd,
            @RequestParam(required = false)
            final Boolean onlyAvailable,
            @RequestParam(required = false) final EventSort sort
    ) {
        log.info("Get events by public api," +
                        " pageParams={},\ntext={},\ncategories={},\npaid={},\nrangeStart={},\nrangeEnd={},\nonlyAvailable={},\nsort={}",
                params, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        return eventService.getEventsPublic(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                request.getRemoteAddr(),
                request.getRequestURI(),
                params
        );
    }
}
