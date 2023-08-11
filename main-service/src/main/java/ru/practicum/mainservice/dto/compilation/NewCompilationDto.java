package ru.practicum.mainservice.dto.compilation;

import javax.validation.constraints.NotBlank;
import java.util.Set;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Data
@ToString
@Builder
public class NewCompilationDto {
    private boolean pinned;
    @NotBlank(message = "Название не может быть пустым")
    @Length(max = 50, message = "Название не может быть длинее 50 символов")
    private final String title;
    private final Set<Long> events;
}