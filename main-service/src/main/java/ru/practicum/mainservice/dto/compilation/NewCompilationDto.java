package ru.practicum.mainservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

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