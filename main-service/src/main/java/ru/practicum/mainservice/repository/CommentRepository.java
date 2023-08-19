package ru.practicum.mainservice.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Integer countById(Long commentId);

    List<Comment> findAllByEventIdOrderByCreatedOnDesc(Long eventId, PageRequest of);

    Optional<Comment> findByEventIdAndAuthorId(Long eventId, Long userId);

    List<Comment> findTop10ByEventIdOrderByCreatedOnDesc(Long eventId);

    List<Comment> findAllByEventIdOrderByCreatedOnDesc(Long eventId);
}
