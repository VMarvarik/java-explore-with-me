package ru.practicum.mainservice.service.interfaces;

import ru.practicum.mainservice.dto.user.NewUserDto;
import ru.practicum.mainservice.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserDto newUserRequestDto);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers(List<Long> userIds, Integer from, Integer size);
}