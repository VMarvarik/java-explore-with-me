package ru.practicum.mainservice.dto.user;

import lombok.*;

@AllArgsConstructor
@Data
@Builder
@ToString
public class UserShortDto {
    private final String name;
    private final String email;
}
