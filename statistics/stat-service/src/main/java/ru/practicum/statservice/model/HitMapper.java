package ru.practicum.statservice.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.statdto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface HitMapper {
    HitMapper INSTANCE = Mappers.getMapper(HitMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", source = "timestamp", qualifiedByName = "stringToLocalDateTime")
    EndpointHit toHitModel(EndpointHitDto endpointHitDto);

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String timestamp) {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
