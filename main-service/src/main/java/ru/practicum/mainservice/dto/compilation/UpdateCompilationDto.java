package ru.practicum.mainservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;

/**
 * DTO for creating new {@link ru.practicum.mainservice.entity.Compilation} in DB
 */
@AllArgsConstructor
@Getter
@ToString
public class UpdateCompilationDto {
    private final Boolean pinned;
    @Length(min = 1, max = 50, message = "Compilation title can't be longer than 50 and shorter than 1 characters")
    private final String title;
    private final HashSet<Long> events;
}
