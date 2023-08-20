package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.mainservice.dto.comment.CommentRequestDto;
import ru.practicum.mainservice.dto.comment.CommentResponseDto;
import ru.practicum.mainservice.exception.DataAccessException;
import ru.practicum.mainservice.exception.DataException;
import ru.practicum.mainservice.mapper.CommentMapper;
import ru.practicum.mainservice.model.Comment;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.Request;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.model.enums.EventState;
import ru.practicum.mainservice.model.enums.RequestStatus;
import ru.practicum.mainservice.repository.CommentRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.interfaces.CommentService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.mainservice.service.UtilityClass.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;


    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, Long eventId, CommentRequestDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Попытка добавить комментарий от несуществующего пользователя");
                    return new EntityNotFoundException(USER_NOT_FOUND);
                }
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> {
                    log.warn("Попытка добавить комментарий к несуществующему событию");
                    return new EntityNotFoundException(EVENT_NOT_FOUND);
                }
        );
        if (event.getState() != EventState.PUBLISHED) {
            log.warn("Попытка добавить комментарий к событию, которое еще не опубликовано");
            throw new DataException("Событие еще не опубликовано");
        }

        if (!Objects.equals(user.getId(), event.getInitiator().getId())) {
            List<Request> requests = requestRepository.findAllByEventIdAndStatusAndRequesterId(eventId, RequestStatus.CONFIRMED, userId);
            if (requests.isEmpty()) {
                log.warn("Попытка добавить комментарий пользователем, который не имеет отношения к событию");
                throw new DataAccessException("Вы не учавствовали в событии или не являетесь автором события");
            }
        }
        Optional<Comment> foundComment = commentRepository.findByEventIdAndAuthorId(eventId, userId);
        if (foundComment.isPresent()) {
            throw new DataAccessException("Можно оставить только один комментарий");
        }
        return CommentMapper.INSTANCE.toDto(commentRepository.save(CommentMapper.INSTANCE.fromDto(commentDto, userId, eventId)));
    }

    @Override
    @Transactional
    public void deleteCommentById(Long commentId, Long userId) {
        Comment comment = checkIfCommentExist(commentId);
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
    public CommentResponseDto updateComment(Long commentId, Long userId, CommentRequestDto commentDto) {
        Comment foundComment = checkIfCommentExist(commentId);

        checkIfUserIsTheAuthor(foundComment.getAuthor().getId(), userId);

        String newText = commentDto.getText();
        if (StringUtils.hasLength(newText)) {
            foundComment.setText(newText);
        }
        return CommentMapper.INSTANCE.toDto(foundComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(EVENT_NOT_FOUND)
        );

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Comment> comments = commentRepository.findAllByEventIdOrderByCreatedOnDesc(eventId, pageRequest);

        return CommentMapper.INSTANCE.toDtos(comments);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getLast10CommentsByEventId(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(EVENT_NOT_FOUND)
        );
        List<Comment> comments = commentRepository.findTop10ByEventIdOrderByCreatedOnDesc(eventId);
        return CommentMapper.INSTANCE.toDtos(comments);
    }

    private void checkIfUserIsTheAuthor(Long authorId, Long userId) {
        if (!Objects.equals(authorId, userId)) {
            log.warn("Попытка доступа к чужому комментарию");
            throw new DataAccessException("Пользовтель не является автором комментария");
        }
    }

    private Comment checkIfCommentExist(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.warn("Попытка удалить несуществующий комментарий");
                    throw new EntityNotFoundException(COMMENT_NOT_FOUND);
                });
    }
}
