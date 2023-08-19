package ru.practicum.mainservice.service.interfaces;


import ru.practicum.mainservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, CommentDto commentDto);

    CommentDto updateComment(Long commentId, Long userId, CommentDto commentDto);

    void deleteCommentById(Long commentId, Long userId);

    void deleteCommentByAdmin(Long commentId);


    List<CommentDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size);

    List<CommentDto> getLast10CommentsByEventId(Long eventId);
}
