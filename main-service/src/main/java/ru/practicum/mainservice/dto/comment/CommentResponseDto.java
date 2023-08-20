package ru.practicum.mainservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.mainservice.service.UtilityClass.PATTERN;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private LocalDateTime createdOn;

    @NotNull
    private Long event;

    @NotNull
    private Long author;

    private String text;
}
