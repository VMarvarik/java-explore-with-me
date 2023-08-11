package ru.practicum.mainservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Full DTO for {@link ru.practicum.mainservice.entity.User}
 */
@AllArgsConstructor
@Getter
public class UserDto {
    private final Long id;
    private final String name;
    private final String email;
}