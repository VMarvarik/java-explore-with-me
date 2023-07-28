package ru.practicum.statdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EndpointHitDto {
    @NotBlank(message = "app не может быть пустым или null")
    private String app;
    @NotBlank(message = "uri не может быть пустым или null")
    private String uri;
    @NotBlank(message = "ip не может быть пустым или null")
    private String ip;
    @NotBlank(message = "timestamp не может быть пустым или null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
