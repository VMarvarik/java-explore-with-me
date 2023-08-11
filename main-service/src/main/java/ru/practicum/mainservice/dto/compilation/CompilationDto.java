package ru.practicum.mainservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.mainservice.dto.event.EventShortDto;

import java.util.List;

@AllArgsConstructor
@Getter
public class CompilationDto {
    private final Long id;
    private final Boolean pinned;
    private final String title;
    private final List<EventShortDto> events;
}
