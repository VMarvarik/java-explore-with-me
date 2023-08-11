package ru.practicum.mainservice.dto.compilation;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;


@Data
@ToString
@Builder
@AllArgsConstructor
public class CompilationUpdateDto {
    private final Boolean pinned;
    @Length(min = 1, max = 50, message = "Название не может быть короче 1 символа или длинее 50")
    private final String title;
    private final HashSet<Long> events;
}
