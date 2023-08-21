package ru.practicum.mainservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.comment.CommentResponseDto;
import ru.practicum.mainservice.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@ToString
@Builder
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
    private List<CommentResponseDto> comments;
}