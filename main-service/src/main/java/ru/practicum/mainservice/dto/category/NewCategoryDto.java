package ru.practicum.mainservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * DTO for creating new {@link ru.practicum.mainservice.entity.Category} in DB.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class NewCategoryDto {
    @Length(min = 1, max = 50, message = "Name should be between 1 and 50 characters")
    @NotBlank(message = "Name should not be blank")
    private String name;
}