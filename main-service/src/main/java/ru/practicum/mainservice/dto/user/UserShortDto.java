package ru.practicum.mainservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Short DTO for {@link ru.practicum.mainservice.entity.User} with
 * only name and email fields
 */
@AllArgsConstructor
@Getter
public class UserShortDto {
    private final String name;
    private final String email;
}