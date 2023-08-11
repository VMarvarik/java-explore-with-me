package ru.practicum.mainservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventConfirmedRequestDto {
    private Long eventId;
    private Long count;
}
