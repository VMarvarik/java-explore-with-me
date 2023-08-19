package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.event.ConfirmedEventDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.enums.RequestStatus;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.statclient.StatClient;
import ru.practicum.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class UtilityClass {
    private final RequestRepository requestRepository;
    private final StatClient statClient = new StatClient();

    private static final String START = "1970-01-01 00:00:00";

    protected static final String USER_NOT_FOUND = "Пользователь не найден";
    protected static final String CATEGORY_NOT_FOUND = "Категория не найдена";
    protected static final String EVENT_NOT_FOUND = "Событие не найдено";
    protected static final String COMPILATION_NOT_FOUND = "Компиляция не найдена";
    protected static final String REQUEST_NOT_FOUND = "Запрос не найден";

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);

    public static String formatTimeToString(LocalDateTime time) {
        return time.format(formatter);
    }

    protected List<EventShortDto> makeEventShortDto(Collection<Event> events) {
        Map<String, Long> viewStatsMap = toViewStats(events);

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

    protected Map<String, Long> toViewStats(Collection<Event> events) {
        List<String> urisToSend = new ArrayList<>();
        for (Event event : events) {
            urisToSend.add(String.format("/events/%s", event.getId()));
        }

        List<ViewStatsDto> viewStats = statClient.getStats(
                START,
                formatTimeToString(LocalDateTime.now()),
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
        List<ConfirmedEventDto> confirmedDtos =
                requestRepository.countConfirmedRequests(eventsIds, RequestStatus.CONFIRMED);
        return confirmedDtos.stream()
                .collect(Collectors.toMap(ConfirmedEventDto::getEventId, ConfirmedEventDto::getCount));
    }
}
