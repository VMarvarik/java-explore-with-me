package ru.practicum.statservice.model;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.statdto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class HitMapper {
    public static EndpointHit toHitModel(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                null,
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                LocalDateTime.parse(endpointHitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}