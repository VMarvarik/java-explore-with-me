package ru.practicum.mainservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.dto.comment.CommentDto;
import ru.practicum.mainservice.model.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mappings({
            @Mapping(target = "event", source = "comment.event.id"),
            @Mapping(target = "author", source = "comment.author.id")
    })
    CommentDto toDto(Comment comment);

    @Mappings({
            @Mapping(target = "text", source = "commentDto.text"),
            @Mapping(target = "author", expression = "java(User.builder().id(userId).build())"),
            @Mapping(target = "event", expression = "java(Event.builder().id(eventId).build())"),
            @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    })
    Comment fromDto(CommentDto commentDto, Long userId, Long eventId);

    List<CommentDto> toDtos(List<Comment> comments);
}
