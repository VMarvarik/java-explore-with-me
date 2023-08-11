package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.event.*;
import ru.practicum.mainservice.entity.*;
import ru.practicum.mainservice.enums.EventSort;
import ru.practicum.mainservice.enums.EventState;
import ru.practicum.mainservice.enums.RequestStatus;
import ru.practicum.mainservice.exception.DataConflictException;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.mapper.LocationMapper;
import ru.practicum.mainservice.repository.*;
import ru.practicum.mainservice.util.PageParams;
import ru.practicum.mainservice.util.TimeManipulator;
import ru.practicum.statclient.StatClient;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class EventService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final ServiceUtility serviceUtility;
    private final StatClient statClient = new StatClient();

    private static final String START = "1970-01-01 00:00:00";
    private static final String APP = "ewm-main-service";

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Getting user by id={}", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("User not found id={}", userId);
                    return new EntityNotFoundException("User not found");
                }
        );
        long categoryId = newEventDto.getCategory();
        log.info("Getting category by id={}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> {
                    log.warn("Category not found id={}", categoryId);
                    return new EntityNotFoundException("Category not found");
                }
        );
        Location location = LocationMapper.INSTANCE.toEntity(newEventDto.getLocation());
        log.info("Saving location to DB");
        location = locationRepository.save(location);
        Event event = EventMapper.INSTANCE.toEntity(newEventDto, user, category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);
        log.info("Saving event to DB");
        // New event can't have views or requests on the moment of creation
        return EventMapper.INSTANCE.toFullDto(eventRepository.save(event), 0L, 0L);
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updater) {
        log.info("Getting event by id={}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Event not found id={}", eventId);
                    return new EntityNotFoundException("Event not found");
                }
        );
        if (event.getState() == EventState.PUBLISHED) {
            log.info("Trying to update published event");
            throw new DataConflictException("Event is published. Can't update it");
        }
        LocalDateTime bound = LocalDateTime.now().plusHours(1);
        log.info("Checking for event with id={} changing category availability", eventId);
        Long newCategoryId = updater.getCategory();
        Category oldCategory = event.getCategory();
        Category newCategory = oldCategory;
        if (newCategoryId != null) {
            if (oldCategory == null || !oldCategory.getId().equals(newCategoryId)) {
                log.info("Getting category by id={}", newCategoryId);
                newCategory = categoryRepository.findById(newCategoryId).orElseThrow(
                        () -> {
                            log.warn("Category not found id={}", newCategoryId);
                            return new EntityNotFoundException("Category not found");
                        }
                );
            }
        }

        log.info("Checking event id={} publishing", eventId);
        EventState newState = event.getState();
        UpdateEventAdminRequestDto.StateInAdminUpd action = updater.getStateAction();
        if (action != null) {
            if (event.getState() != EventState.PENDING) {
                log.info("Trying publish not pending event");
                throw new DataConflictException("Event must be pending");
            } else if (
                    event.getEventDate().isBefore(bound)
                            && action == UpdateEventAdminRequestDto.StateInAdminUpd.PUBLISH_EVENT
            ) {
                log.info("Trying publish event too late");
                throw new DataConflictException("It's too late to publish this event");
            }
            switch (action) {
                case PUBLISH_EVENT:
                    log.info("Setting event state to published");
                    newState = EventState.PUBLISHED;
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    log.info("Setting event state to CANCELED");
                    newState = EventState.CANCELED;
                    break;
                default:
                    log.info("Trying to set invalid state");
                    throw new IllegalArgumentException("Invalid state");
            }
            event.setState(newState);
        }
        log.info("Event id={} state updated to {}, category changed to {} by admin",
                eventId, newState, newCategory);
        event = EventMapper.INSTANCE.partialUpdate(updater, newCategory, newState, event);

        // Unpublished event can't be viewed or requested, published event can't be updated
        return EventMapper.INSTANCE.toFullDto(eventRepository.save(event), 0L, 0L);
    }

    @Transactional
    public EventFullDto updateEventByUser(UpdateEventUserRequestDto updateEventDto, Long eventId, Long userId) {
        log.info("Getting event by id={}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Event not found id={}", eventId);
                    return new EntityNotFoundException("Event not found");
                }
        );
        Long newCategoryId = updateEventDto.getCategory();
        Category oldCategory = event.getCategory();
        Category newCategory = oldCategory;
        if (newCategoryId != null) {
            if (oldCategory == null || !oldCategory.getId().equals(newCategoryId)) {
                log.info("Getting category by id={}", newCategoryId);
                newCategory = categoryRepository.findById(newCategoryId).orElseThrow(
                        () -> {
                            log.warn("Category not found id={}", newCategoryId);
                            return new EntityNotFoundException("Category not found");
                        }
                );
            }
        }

        User initiator = event.getInitiator();
        if (!Objects.equals(initiator.getId(), userId)) {
            log.warn("User with id={} trying update event with id={} where he is not initiator", userId, eventId);
            throw new DataConflictException("User is not the initiator of the event");
        }
        if (event.getState() == EventState.PUBLISHED) {
            log.info("User with id={} trying to update published event with id={}", userId, eventId);
            throw new DataConflictException("Event must not be published");
        }
        EventState newState = event.getState();
        UpdateEventUserRequestDto.StateInUserUpd action = updateEventDto.getStateAction();
        if (action != null) {
            switch (action) {
                case SEND_TO_REVIEW:
                    newState = EventState.PENDING;
                    break;
                case CANCEL_REVIEW:
                    newState = EventState.CANCELED;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid state");
            }
        }
        log.info("Updating event entity");
        event = EventMapper.INSTANCE.partialUpdate(updateEventDto, newCategory, newState, event);
        // Unpublished event can't be viewed or requested, published event can't be updated
        log.info("Saving updated event to DB");
        return EventMapper.INSTANCE.toFullDto(eventRepository.save(event), 0L, 0L);
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByIdByInitiator(Long eventId, Long userId) {
        log.info("Getting event by id={}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Event not found id={}", eventId);
                    return new EntityNotFoundException("Event not found");
                }
        );
        log.info("Checking if user with id={} exists", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User not found id={}", userId);
            throw new EntityNotFoundException("User not found");
        }
        User initiator = event.getInitiator();
        if (!initiator.getId().equals(userId)) {
            log.warn("User with id={} trying get full event with id={} info where he is not initiator", userId, eventId);
            throw new IllegalArgumentException("User is not the initiator of the event");
        }
        log.info("Getting quantity of confirmed requests for event");
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        log.info("Getting quantity of views of event");
        Long views = getViewsForOneEvent(eventId);
        return EventMapper.INSTANCE.toFullDto(event, confirmedRequests, views);
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(Long eventId, String ip, String uri) {
        log.info("Getting event by id={}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Event not found id={}", eventId);
                    return new EntityNotFoundException("Event not found");
                }
        );
        if (event.getState() != EventState.PUBLISHED) {
            log.warn("Trying to get through public endpoint event with id={} which is not published", eventId);
            throw new EntityNotFoundException("Event is not published");
        }
        log.info("Getting quantity of confirmed requests for event");
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        log.info("Sending endpoint hit to stats");
        statClient.addHit(new EndpointHitDto(
                APP,
                uri,
                ip,
                TimeManipulator.formatTimeToString(LocalDateTime.now())
        ));
        log.info("Getting quantity of views of event");
        Long views = getViewsForOneEvent(eventId);
        return EventMapper.INSTANCE.toFullDto(event, confirmedRequests, views);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublic(
            String text,
            List<Long> categoryIds,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSort sort,
            String ip,
            String uri,
            PageParams pageParams
    ) {
        log.info("Sending endpoint hit to stats");
        statClient.addHit(new EndpointHitDto(
                APP,
                uri,
                ip,
                TimeManipulator.formatTimeToString(LocalDateTime.now())
        ));
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(10000);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            log.warn("Got invalid time range");
            throw new IllegalArgumentException("Invalid time range");
        }
        Specification<Event> spec = Specification.where(inStates(List.of(EventState.PUBLISHED)))
                .and(inEventDates(rangeStart, rangeEnd))
                .and(inCategoryIds(categoryIds))
                .and(byPaid(paid))
                .and(byTextInAnnotationOrDescription(text));

        if (onlyAvailable != null && onlyAvailable) {
            spec = spec.and(byParticipantLimit());
        }
        PageRequest pageRequest = PageRequest.of(
                pageParams.getFrom() / pageParams.getSize(),
                pageParams.getSize(),
                Sort.by(Sort.Direction.DESC, "eventDate"));
        log.info("Getting events with filters");
        List<Event> events = eventRepository.findAll(spec, pageRequest).getContent();
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Mapping events to dtos");
        List<EventShortDto> eventShortDtos = serviceUtility.makeEventShortDtos(events);
        if (sort == EventSort.VIEWS) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        return eventShortDtos;
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsInitiatedByUser(Long userId, PageParams pageParams) {
        log.info("Checking if user exists with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User not found");
            throw new EntityNotFoundException("User not found");
        }
        log.info("Getting events initiated by user with id: {}", userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageParams.makePageRequest());
        if (events.isEmpty()) {
            log.info("No events found");
            return new ArrayList<>();
        }

        return serviceUtility.makeEventShortDtos(events);
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventsByAdmin(
            List<Long> users,
            List<Long> categories,
            List<EventState> states,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            PageParams pageParams
    ) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(4000);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(10000);
        }
        Specification<Event> spec = Specification.where(inEventDates(rangeStart, rangeEnd))
                .and(inCategoryIds(categories))
                .and(inStates(states))
                .and(inUserIds(users));
        PageRequest pageRequest = PageRequest.of(
                pageParams.getFrom() / pageParams.getSize(),
                pageParams.getSize(),
                Sort.by(Sort.Direction.DESC, "eventDate"));
        log.info("Getting events with filters");
        List<Event> events = eventRepository.findAll(spec, pageRequest).getContent();
        if (events.isEmpty()) {
            log.info("No events found");
            return new ArrayList<>();
        }
        return makeEventFullDtos(events);
    }

    private Long getViewsForOneEvent(Long eventId) {
        List<String> urisToSend = List.of(String.format("/events/%s", eventId));
        List<ViewStatsDto> viewStats = statClient.getStats(
                START,
                TimeManipulator.formatTimeToString(LocalDateTime.now()),
                urisToSend,
                true
        );
        // On one uri sent by statClient we should get one viewStats in list
        ViewStatsDto viewStatsDto = viewStats == null || viewStats.isEmpty() ? null : viewStats.get(0);
        return viewStatsDto == null || viewStatsDto.getHits() == null ? 0 : viewStatsDto.getHits();
    }

    private List<EventFullDto> makeEventFullDtos(List<Event> events) {
        log.info("Calling stat client to get view stats");
        Map<String, Long> viewStatsMap = serviceUtility.makeViewStatsMap(events);

        Map<Long, Long> confirmedRequests = serviceUtility.getConfirmedRequests(events);

        List<EventFullDto> eventsDto = new ArrayList<>();
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
                    EventMapper.INSTANCE.toFullDto(event, reqCount, views)
            );
        }

        return eventsDto;
    }

    private Specification<Event> inUserIds(List<Long> users) {
        return users == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("initiator").get("id")).value(users);
    }

    private Specification<Event> inCategoryIds(List<Long> categories) {
        return categories == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("category").get("id")).value(categories);
    }

    private Specification<Event> inStates(List<EventState> states) {
        return states == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("state")).value(states);
    }

    private Specification<Event> inEventDates(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return rangeStart == null || rangeEnd == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("eventDate"), rangeStart, rangeEnd);
    }

    private Specification<Event> byPaid(Boolean paid) {
        return paid == null ? null : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid);
    }

    private Specification<Event> byTextInAnnotationOrDescription(String text) {
        return text == null ? null : (root, query, criteriaBuilder) -> {
            String lowerCasedText = text.toLowerCase();
            Expression<String> annotation = criteriaBuilder.lower(root.get("annotation"));
            Expression<String> description = criteriaBuilder.lower(root.get("description"));
            return criteriaBuilder.or(
                    criteriaBuilder.like(annotation, "%" + lowerCasedText + "%"),
                    criteriaBuilder.like(description, "%" + lowerCasedText + "%")
            );
        };
    }

    private Specification<Event> byParticipantLimit() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Request> subRoot = sub.from(Request.class);
            sub.select(criteriaBuilder.count(subRoot.get("id"))).where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(subRoot.get("status"), RequestStatus.CONFIRMED),
                            criteriaBuilder.equal(subRoot.get("event").get("id"), root.get("id"))
                    )
            );
            return criteriaBuilder.greaterThan(root.get("participantLimit"), sub);
        };
    }
}
