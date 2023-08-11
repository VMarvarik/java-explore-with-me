package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.dto.EventConfirmedRequestDto;
import ru.practicum.mainservice.entity.Request;
import ru.practicum.mainservice.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Long countByEventId(Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    @Query(value = "SELECT new ru.practicum.mainservice.dto.EventConfirmedRequestDto(r.event.id, count(r.event.id)) " +
            "FROM Request r " +
            "WHERE r.event.id IN :eventIds " +
            "AND r.status = :status " +
            "GROUP BY r.event.id")
    List<EventConfirmedRequestDto> countConfirmedRequests(List<Long> eventIds, RequestStatus status);
}