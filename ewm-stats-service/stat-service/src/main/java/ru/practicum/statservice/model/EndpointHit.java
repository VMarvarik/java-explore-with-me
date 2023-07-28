package ru.practicum.statservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "hit")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String app;
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    private LocalDateTime created;
}
