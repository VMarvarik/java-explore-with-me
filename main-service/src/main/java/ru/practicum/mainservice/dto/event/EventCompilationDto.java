package ru.practicum.mainservice.dto.event;

import lombok.*;

@AllArgsConstructor
@Data
@ToString
@Builder
public class EventCompilationDto {
    private final Long eventId;
    private final Long compilationId;
}
