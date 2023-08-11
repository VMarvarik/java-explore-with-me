package ru.practicum.mainservice.model;

import lombok.*;
import ru.practicum.mainservice.model.enums.RequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created")
    private LocalDateTime created;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id")
    private User requester;

    @NotNull
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private RequestStatus status;

    public  Request(Event event, User requester, RequestStatus status) {
        this.id = null;
        this.created = LocalDateTime.now();
        this.event = event;
        this.requester = requester;
        this.status = status;
    }
}
