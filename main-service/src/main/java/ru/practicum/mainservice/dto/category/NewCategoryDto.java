package ru.practicum.mainservice.dto.category;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @Size(min = 1, max = 50, message = "Имя не может быть длиной более 50 символов")
    @NotBlank(message = "Имя не может быть пустым или null")
    private String name;
}
