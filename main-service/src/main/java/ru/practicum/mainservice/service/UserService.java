package ru.practicum.mainservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.user.NewUserRequestDto;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.mapper.UserMapper;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.util.PageParams;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        log.info("Mappings from NewUserRequestDto={} to User", newUserRequestDto);
        User user = UserMapper.INSTANCE.toUser(newUserRequestDto);
        log.info("Saving user to DB and mapping to UserDto");
        return UserMapper.INSTANCE.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found", userId);
            throw new EntityNotFoundException(String.format("User with id=%s was not found", userId));
        } else {
            log.info("Deleting user with id={}", userId);
            userRepository.deleteById(userId);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> userIds, PageParams pageParams) {
        List<User> users;
        if (userIds == null || userIds.isEmpty()) {
            log.info("Getting all users from DB with pagination params={}", pageParams);
            users = userRepository.findAll(pageParams.makePageRequest()).getContent();
        } else {
            log.info("Getting users from DB with ids={}", userIds);
            users = userRepository.findAllById(userIds);
        }
        return users.stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }
}
