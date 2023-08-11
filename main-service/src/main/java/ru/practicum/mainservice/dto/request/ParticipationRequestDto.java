package ru.practicum.mainservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.mainservice.enums.RequestStatus;

import java.time.LocalDateTime;

/**
 * Full DTO for {@link ru.practicum.mainservice.entity.Request}
 */
@AllArgsConstructor
@Getter
public class ParticipationRequestDto {
    private final Long id;
    private final LocalDateTime created;
    private final Long event;
    private final Long requester;
    private final RequestStatus status;
}