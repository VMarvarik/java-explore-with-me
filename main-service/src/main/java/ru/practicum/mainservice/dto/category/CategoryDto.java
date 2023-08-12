package ru.practicum.mainservice.dto.category;

import lombok.*;

import javax.validation.constraints.Size;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @Size(min = 1, max = 50, message = "Имя не может быть длиной более 50 символов")
    private String name;
}
