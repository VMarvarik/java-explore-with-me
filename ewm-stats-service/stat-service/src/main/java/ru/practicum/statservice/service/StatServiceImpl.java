package ru.practicum.statservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.model.HitMapper;
import ru.practicum.statservice.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    @Override
    public void addHit(EndpointHitDto hitDto) {
        log.trace("Сохранение хит: {}", hitDto);
        repository.save(HitMapper.toHitModel(hitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(ru.practicum.statservice.model.StatForRequest params) {
        List<String> uris = params.getUris();
        LocalDateTime start = params.getStart();
        LocalDateTime end = params.getEnd();
        if (params.getUnique()) {
            if (uris.isEmpty()) {
                return repository.getAllStatsByDistinctIp(start, end);
            } else {
                return repository.getAllStatsInUrisByDistinctIp(uris, start, end);
            }
        } else {
            if (uris.isEmpty()) {
                return repository.getAllStats(start, end);
            } else {
                return repository.getAllStatsInUris(uris, start, end);
            }
        }
    }
}
