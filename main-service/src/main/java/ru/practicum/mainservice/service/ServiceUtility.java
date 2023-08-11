package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.EventConfirmedRequestDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.enums.RequestStatus;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.util.TimeManipulator;
import ru.practicum.statclient.StatClient;
import ru.practicum.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class ServiceUtility {
    private final RequestRepository requestRepository;
    private final StatClient statClient = new StatClient();

    private static final String START = "1970-01-01 00:00:00";

    protected List<EventShortDto> makeEventShortDtos(Collection<Event> events) {
        log.info("Calling stat client to get view stats");
        Map<String, Long> viewStatsMap = makeViewStatsMap(events);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);

        List<EventShortDto> eventsDto = new ArrayList<>();
        for (Event event : events) {
            Long eventId = event.getId();
            Long reqCount = confirmedRequests.get(eventId);
            Long views = viewStatsMap.get(String.format("/events/%s", eventId));
            if (reqCount == null) {
                reqCount = 0L;
            }
            if (views == null) {
                views = 0L;
            }
            eventsDto.add(
                    EventMapper.INSTANCE.toShortDto(event, reqCount, views)
            );
        }

        return eventsDto;
    }

    protected Map<String, Long> makeViewStatsMap(Collection<Event> events) {
        List<String> urisToSend = new ArrayList<>();
        for (Event event : events) {
            urisToSend.add(String.format("/events/%s", event.getId()));
        }

        List<ViewStatsDto> viewStats = statClient.getStats(
                START,
                TimeManipulator.formatTimeToString(LocalDateTime.now()),
                urisToSend,
                true
        );

        return viewStats.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
    }

    protected Map<Long, Long> getConfirmedRequests(Collection<Event> events) {
        List<Long> eventsIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        log.info("Getting confirmed requests for events with ids: {}", eventsIds);
        List<EventConfirmedRequestDto> confirmedRequestDtos =
                requestRepository.countConfirmedRequests(eventsIds, RequestStatus.CONFIRMED);
        return confirmedRequestDtos.stream()
                .collect(Collectors.toMap(EventConfirmedRequestDto::getEventId, EventConfirmedRequestDto::getCount));
    }
}
