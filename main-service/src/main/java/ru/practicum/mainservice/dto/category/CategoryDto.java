package ru.practicum.mainservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.entity.Category;

/**
 * DTO for {@link Category}
 */
@AllArgsConstructor
@Getter
@ToString
public class CategoryDto {
    private final Long id;
    @Length(message = "Name should be between 1 and 50 chars", min = 1, max = 50)
    private final String name;
}