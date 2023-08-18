package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.mainservice.dto.comment.CommentDto;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.exception.AccessDeniedException;
import ru.practicum.mainservice.exception.DataException;
import ru.practicum.mainservice.mapper.CommentMapper;
import ru.practicum.mainservice.model.Comment;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.model.enums.EventState;
import ru.practicum.mainservice.model.enums.RequestStatus;
import ru.practicum.mainservice.repository.CommentRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.interfaces.CommentService;
import ru.practicum.mainservice.service.interfaces.RequestService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.mainservice.service.UtilityClass.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;
    private final RequestService requestService;

    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(USER_NOT_FOUND)
        );

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(EVENT_NOT_FOUND)
        );

        if (event.getState() != EventState.PUBLISHED) {
            throw new DataException("Событие еще не опубликовано");
        }

        List<RequestDto> requests = requestService.getAllUserRequests(userId);

        requests.stream().filter(requestDto -> requestDto.getStatus() == RequestStatus.CONFIRMED
                && !requestDto.getEvent().equals(eventId)).findAny().orElseThrow(()
                -> new EntityNotFoundException("REQUEST_NOT_FOUND"));

        if (!Objects.equals(user.getId(), event.getInitiator().getId())) {
            throw new AccessDeniedException("Пользователь не учавствовал в событии");
        }

        Optional<Comment> foundComment = commentRepository.findByEventIdAndAuthorId(eventId, userId);

        if (foundComment.isPresent()) {
            throw new AccessDeniedException("Можно оставить только один комменатрий");
        }
        return commentMapper.toDto(commentRepository.save(commentMapper.fromDto(commentDto, userId, eventId)));
    }

    @Override
    @Transactional
    public void deleteCommentById(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(COMMENT_NOT_FOUND)
        );

        checkIfUserIsTheAuthor(comment.getAuthor().getId(), userId);

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        checkIfCommentExist(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, Long userId, CommentDto commentDto) {
        Comment foundComment = checkIfCommentExist(commentId);

        checkIfUserIsTheAuthor(foundComment.getAuthor().getId(), userId);

        String newText = commentDto.getText();
        if (StringUtils.hasLength(newText)) {
            foundComment.setText(newText);
        }

        Comment savedComment = commentRepository.save(foundComment);
        return commentMapper.toDto(savedComment);
    }

    public List<CommentDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(EVENT_NOT_FOUND)
        );

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Comment> comments = commentRepository.findAllByEventIdOrderByCreatedOnDesc(eventId, pageRequest);

        return commentMapper.toDtos(comments);
    }

    public List<CommentDto> getLast10CommentsByEventId(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(EVENT_NOT_FOUND)
        );
        List<Comment> comments = commentRepository.findTop10ByEventIdOrderByCreatedOnDesc(eventId);
        return commentMapper.toDtos(comments);
    }

    private void checkIfUserIsTheAuthor(Long authorId, Long userId) {
        if (!Objects.equals(authorId, userId)) {
            throw new AccessDeniedException("Пользовтель не является автором комментария");
        }
    }

    private Comment checkIfCommentExist(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(COMMENT_NOT_FOUND));
    }
}
