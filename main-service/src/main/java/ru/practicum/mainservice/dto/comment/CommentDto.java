package ru.practicum.mainservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.mainservice.dto.event.EventDto;
import ru.practicum.mainservice.dto.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private EventDto event;

    private UserDto author;

    @Size(min = 10, max = 2000, message = "Размер комментария от 10 до 2000 символов")
    @NotBlank(message = "Комментарий не может быть пустым или отсутствовать")
    private String text;
}