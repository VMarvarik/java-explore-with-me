package ru.practicum.mainservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;


@AllArgsConstructor
@Getter
@ToString
public class NewCompilationDto {
    private boolean pinned;
    @NotBlank(message = "Compilation title can't be empty")
    @Length(max = 50, message = "Compilation title can't be longer than 50 characters")
    private final String title;
    private final Set<Long> events;
}