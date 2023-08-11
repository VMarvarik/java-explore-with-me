package ru.practicum.statservice.service;

import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.utils.StatRequestParams;

import java.util.List;

public interface StatService {
    void addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(StatRequestParams params);
}
