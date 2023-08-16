package ru.practicum.mainservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.dto.user.NewUserDto;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.dto.user.UserShortDto;
import ru.practicum.mainservice.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserShortDto toShortDto(User user);

    UserDto toDto(User user);

    User toModel(NewUserDto newUserRequestDto);
}
