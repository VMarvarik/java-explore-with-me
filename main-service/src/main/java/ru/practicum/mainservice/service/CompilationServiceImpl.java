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
import ru.practicum.mainservice.service.interfaces.CompilationService;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.service.UtilityClass.COMPILATION_NOT_FOUND;

@AllArgsConstructor
@Slf4j
@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final UtilityClass serviceUtility;

    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Set<Long> eventsIds = newCompilationDto.getEvents();
        Compilation compilation = CompilationMapper.INSTANCE.toModel(newCompilationDto);
        if (eventsIds != null) {
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(new HashSet<>(events));
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        List<EventShortDto> eventsDto = serviceUtility.makeEventShortDto(savedCompilation.getEvents());
        return CompilationMapper.INSTANCE.toDto(savedCompilation, eventsDto);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new EntityNotFoundException(COMPILATION_NOT_FOUND);
        }
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(COMPILATION_NOT_FOUND)
        );

        Set<Long> eventsIds = updateCompilationDto.getEvents();
        if (eventsIds != null) {
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(new HashSet<>(events));
        }

        CompilationMapper.INSTANCE.forUpdate(updateCompilationDto, compilation);

        List<EventShortDto> eventsDto = serviceUtility.makeEventShortDto(compilation.getEvents());

        Set<Event> updatedEvents = compilationRepository.save(compilation).getEvents();

        return CompilationMapper.INSTANCE.toDto(compilation, eventsDto);
    }


    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(COMPILATION_NOT_FOUND)
        );
        List<EventShortDto> eventsDto = serviceUtility.makeEventShortDto(compilation.getEvents());
        return CompilationMapper.INSTANCE.toDto(compilation, eventsDto);
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }

        if (compilations.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Event> events = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());
        List<EventShortDto> eventsDtoList = serviceUtility.makeEventShortDto(events);

        Map<Long, EventShortDto> eventDtosMap = new HashMap<>();
        for (EventShortDto eventShortDto : eventsDtoList) {
            eventDtosMap.put(
                    eventShortDto.getId(),
                    eventShortDto
            );
        }

        Map<Long, List<EventShortDto>> eventsDtoMapByCompilationId = compilations.stream()
                .collect(Collectors.toMap(Compilation::getId, compilation -> {
                    Set<Event> eventsSet = compilation.getEvents();
                    return eventsSet.stream()
                            .map(event -> eventDtosMap.get(event.getId()))
                            .collect(Collectors.toList());
                }));

        return compilations.stream()
                .map(compilation -> {
                    Long compilationId = compilation.getId();
                    List<EventShortDto> eventShortDtos = eventsDtoMapByCompilationId.get(compilationId);
                    return CompilationMapper.INSTANCE.toDto(compilation, eventShortDtos);
                }).collect(Collectors.toList());
    }
}
