package ru.practicum.statservice.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.statdto.EndpointHitDto;

@Mapper
public interface HitMapper {
    HitMapper INSTANCE = Mappers.getMapper(HitMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", source = "timestamp")
    EndpointHit toHitModel(EndpointHitDto endpointHitDto);
}
