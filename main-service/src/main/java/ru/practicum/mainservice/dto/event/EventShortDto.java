package ru.practicum.mainservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.comment.CommentDto;
import ru.practicum.mainservice.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.Set;

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
    private Set<CommentDto> comments;
}