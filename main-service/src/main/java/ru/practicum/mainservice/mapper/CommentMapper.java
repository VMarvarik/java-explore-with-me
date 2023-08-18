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
            @Mapping(target = "event", source = "event"),
            @Mapping(target = "author", source = "author")
    })
    CommentDto toDto(Comment comment);

    @Mappings({
            @Mapping(target = "text", source = "text"),
            @Mapping(target = "author", expression = "author"),
            @Mapping(target = "event", expression = "event"),
            @Mapping(target = "createdOn", expression = "createdOn")
    })
    Comment fromDto(CommentDto commentDto, Long userId, Long eventId);

    List<CommentDto> toDtos(List<Comment> comments);
}
