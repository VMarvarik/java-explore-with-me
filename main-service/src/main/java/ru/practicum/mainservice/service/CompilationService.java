package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.CompilationUpdateDto;
import ru.practicum.mainservice.dto.compilation.NewCompilationDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.mapper.CompilationMapper;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.repository.CompilationRepository;
import ru.practicum.mainservice.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final UtilityClass serviceUtility;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Set<Long> eventsIds = newCompilationDto.getEvents();
        log.info("Mapping new compilation DTO={} to Compilation entity", newCompilationDto);
        Compilation compilation = CompilationMapper.INSTANCE.toModel(newCompilationDto);
        if (eventsIds != null) {
            log.info("Getting all events by ids {}", eventsIds);
            List<Event> events = eventRepository.findAllById(eventsIds);
            log.info("Setting events to Compilation");
            compilation.setEvents(new HashSet<>(events));
        }
        log.info("Saving compilation={} to DB", compilation);
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Compilation saved to DB. Starting making DTO");
        List<EventShortDto> eventsDto = serviceUtility.makeEventShortDto(savedCompilation.getEvents());
        log.info("Making compilation dto");
        return CompilationMapper.INSTANCE.toDto(savedCompilation, eventsDto);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            log.warn("Compilation with id={} not found", compId);
            throw new EntityNotFoundException(String.format("Compilation with id=%s not found", compId));
        }
        log.info("Deleting compilation with id={} from DB", compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto updateCompilationDto) {
        log.info("Getting compilation with id={} from DB", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> {
                    log.warn("Compilation not found");
                    return new EntityNotFoundException("Compilation not found");
                }
        );
        Set<Long> eventsIds = updateCompilationDto.getEvents();
        if (eventsIds != null) {
            log.info("Getting events by ids {} for compilation update", eventsIds);
            List<Event> events = eventRepository.findAllById(eventsIds);
            log.info("Setting events to Compilation");
            compilation.setEvents(new HashSet<>(events));
        }
        log.info("Updating compilation={} with updateCompilationDto={} and saving to DB", compilation, updateCompilationDto);
        compilation = CompilationMapper.INSTANCE.forUpdate(updateCompilationDto, compilation);

        log.info("Saving updated compilation={} to DB", compilation);
        compilation = compilationRepository.save(compilation);
        log.info("Compilation saved to DB. Starting making DTO");
        List<EventShortDto> eventsDto = serviceUtility.makeEventShortDto(compilation.getEvents());
        log.info("Making compilation dto");
        return CompilationMapper.INSTANCE.toDto(compilation, eventsDto);
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        log.info("Getting compilation with id={} from DB", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> {
                    log.warn("Compilation not found");
                    return new EntityNotFoundException("Compilation not found");
                }
        );
        log.info("Compilation got. Starting making DTO");
        List<EventShortDto> eventsDto = serviceUtility.makeEventShortDto(compilation.getEvents());
        log.info("Making compilation dto");
        return CompilationMapper.INSTANCE.toDto(compilation, eventsDto);
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, PageParams pageParams) {
        List<Compilation> compilations;
        PageRequest pageRequest = pageParams.makePageRequest();
        if (pinned != null) {
            log.info("Getting compilations by pinned={}, page params={}", pinned, pageParams);
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        } else {
            log.info("Getting compilations with page params={}", pageParams);
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }

        if (compilations.isEmpty()) {
            log.info("Compilations not found");
            return new ArrayList<>();
        }
        log.info("Compilations found. Collecting unique events");
        Set<Event> events = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());
        List<EventShortDto> eventsDtoList = serviceUtility.makeEventShortDto(events);

        log.info("Mapping events to map of event id - event short dto");
        Map<Long, EventShortDto> eventDtosMap = new HashMap<>();
        for (EventShortDto eventShortDto : eventsDtoList) {
            eventDtosMap.put(
                    eventShortDto.getId(),
                    eventShortDto
            );
        }

        log.info("Mapping compilations to map of compilation id - list of event short dto");
        Map<Long, List<EventShortDto>> eventsDtoMapByCompilationId = compilations.stream()
                .collect(Collectors.toMap(Compilation::getId, compilation -> {
                    Set<Event> eventsSet = compilation.getEvents();
                    return eventsSet.stream()
                            .map(event -> eventDtosMap.get(event.getId()))
                            .collect(Collectors.toList());
                }));

        log.info("Mapping compilations to list of compilation dto");
        return compilations.stream()
                .map(compilation -> {
                    Long compilationId = compilation.getId();
                    List<EventShortDto> eventShortDtos = eventsDtoMapByCompilationId.get(compilationId);
                    return CompilationMapper.INSTANCE.toDto(compilation, eventShortDtos);
                }).collect(Collectors.toList());
    }
}