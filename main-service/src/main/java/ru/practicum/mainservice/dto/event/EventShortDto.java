package ru.practicum.mainservice.dto.event;

import lombok.*;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.user.UserShortDto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Data
@ToString
@Builder
public class EventShortDto {
    private final Long id;
    private final String annotation;
    private final CategoryDto category;
    private final Long confirmedRequests;
    private final LocalDateTime eventDate;
    private final UserShortDto initiator;
    private final Boolean paid;
    private final String title;
    private final Long views;
}