package ru.practicum.mainservice.dto.event;

import lombok.*;

@AllArgsConstructor
@Data
@ToString
@Builder
public class ConfirmedEventDto {
    private Long eventId;
    private Long count;
}
