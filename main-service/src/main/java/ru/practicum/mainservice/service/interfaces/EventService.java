package ru.practicum.mainservice.service.interfaces;

import ru.practicum.mainservice.dto.event.EventDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.EventUpdateRequestDto;
import ru.practicum.mainservice.dto.event.NewEventDto;
import ru.practicum.mainservice.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDto addEvent(Long userId, NewEventDto newEventDto);

    EventDto updateEventByAdmin(Long eventId, EventUpdateRequestDto updater);

    EventDto updateEventByUser(EventUpdateRequestDto updateEventDto, Long eventId, Long userId);

    EventDto getEventByIdByInitiator(Long eventId, Long userId);

    EventDto getEventByIdPublic(Long eventId, String ip, String uri);

    List<EventShortDto> getEventsPublic(String text, List<Long> categoryIds, Boolean paid, LocalDateTime start,
            LocalDateTime end, Boolean onlyAvailable, String sort, String ip, String uri, Integer from, Integer size);

    List<EventShortDto> getEventsInitiatedByUser(Long userId, Integer from, Integer size);

    List<EventDto> getAllEventsByAdmin(List<Long> users, List<Long> categories, List<EventState> states,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
