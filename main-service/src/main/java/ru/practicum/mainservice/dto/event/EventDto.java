package ru.practicum.mainservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.comment.CommentDto;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.dto.user.UserShortDto;
import ru.practicum.mainservice.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@Data
@ToString
@Builder
public class EventDto {
    private final Long id;
    private final String annotation;
    private final CategoryDto category;
    private final Long confirmedRequests;
    private final LocalDateTime createdOn;
    private final String description;
    private final LocalDateTime eventDate;
    private final UserShortDto initiator;
    private final LocationDto location;
    private final Boolean paid;
    private final Integer participantLimit;
    private final LocalDateTime publishedOn;
    private final Boolean requestModeration;
    private final EventState state;
    private final String title;
    private final Long views;
    private final List<CommentDto> comments;
}
