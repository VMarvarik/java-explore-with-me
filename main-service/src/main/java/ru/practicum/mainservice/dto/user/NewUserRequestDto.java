package ru.practicum.mainservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO for creating new {@link ru.practicum.mainservice.entity.User} in DB.
 */
@AllArgsConstructor
@Getter
@ToString
public class NewUserRequestDto {
    @Length(min = 2, max = 250, message = "Name must be from 2 to 250 chars")
    @NotBlank(message = "Name is mandatory")
    private final String name;
    @Length(min = 6, max = 254, message = "Name must be from 6 to 254 chars")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email is not valid")
    private final String email;
}