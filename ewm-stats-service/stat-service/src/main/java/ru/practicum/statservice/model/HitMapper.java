package ru.practicum.statservice.model;

import ru.practicum.statdto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@Mapper(componentModel = "spring")
//public interface HitMapper {
//    HitMapper INSTANCE = Mappers.getMapper(HitMapper.class);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "created", source = "timestamp")
//    EndpointHit toHitModel(EndpointHitDto endpointHitDto);
//}
public class HitMapper {
    public static EndpointHit toHitModel(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .id(null)
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .created(LocalDateTime.parse(endpointHitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
