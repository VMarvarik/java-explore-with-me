package ru.practicum.mainservice.dto.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
@Builder
@ToString
public class NewUserDto {
    @Length(min = 2, max = 250, message = "Размер имени от 2 до 250 символов")
    @NotBlank(message = "Имя не может быть пустым или отсутствовать")
    private final String name;
    @Length(min = 6, max = 254, message = "Размер эмейл от 6 до 254 знаков")
    @NotBlank(message = "Email не может быть пустым или отсутствовать")
    @Email(message = "Неправильный тип эмейл")
    private final String email;
}
