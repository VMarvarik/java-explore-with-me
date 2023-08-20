package ru.practicum.mainservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.mainservice.service.UtilityClass.PATTERN;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private LocalDateTime createdOn;

    @NotBlank
    private Long event;

    @NotBlank
    private Long author;

    @Size(min = 10, max = 2000, message = "Размер комментария от 10 до 2000 символов")
    @NotBlank(message = "Комментарий не может быть пустым или отсутствовать")
    private String text;
}
