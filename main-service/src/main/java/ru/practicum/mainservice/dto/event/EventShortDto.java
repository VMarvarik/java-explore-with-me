package ru.practicum.mainservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.user.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
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
