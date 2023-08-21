package ru.practicum.mainservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.dto.comment.CommentRequestDto;
import ru.practicum.mainservice.dto.comment.CommentResponseDto;
import ru.practicum.mainservice.model.Comment;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.User;

import java.util.List;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "author.id", target = "author")
    CommentResponseDto toDto(Comment comment);

    List<CommentResponseDto> toDtos(List<Comment> comments);

    @Mapping(target = "event", expression = "java(eventFromId(eventId))")
    @Mapping(target = "author", expression = "java(userFromId(userId))")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    Comment fromDto(CommentRequestDto commentDto, Long userId, Long eventId);

    default Event eventFromId(Long eventId) {
        if (eventId == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventId);
        return event;
    }

    default User userFromId(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
