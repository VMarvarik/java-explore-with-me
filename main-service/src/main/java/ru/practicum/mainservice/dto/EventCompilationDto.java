package ru.practicum.mainservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventCompilationDto {
    private final Long eventId;
    private final Long compilationId;
}
