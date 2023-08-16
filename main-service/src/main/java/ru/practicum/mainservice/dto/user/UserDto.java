package ru.practicum.mainservice.dto.user;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    private String email;

    private String name;
}
