package ru.practicum.statservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.model.StatMapper;
import ru.practicum.statservice.model.StatRequestParams;
import ru.practicum.statservice.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        log.trace("Saving endpoint hit to db: {}", endpointHitDto);
        repository.save(StatMapper.toEndpointHitEntity(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(StatRequestParams params) {
        List<String> uris = params.getUris();
        LocalDateTime start = params.getStart();
        LocalDateTime end = params.getEnd();
        if (params.getUnique()) {
            if (uris == null || uris.isEmpty()) {
                log.debug("Getting all stats by unique ip from {} to {}", start, end);
                return repository.getAllStatsByDistinctIp(start, end);
            } else {
                log.debug("Getting all stats by unique ip from {} to {} in uris {}", start, end, uris);
                return repository.getAllStatsInUrisByDistinctIp(uris, start, end);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                log.debug("Getting all stats from {} to {}", start, end);
                return repository.getAllStats(start, end);
            } else {
                log.debug("Getting all stats from {} to {} in uris {}", start, end, uris);
                return repository.getAllStatsInUris(uris, start, end);
            }
        }
    }
}
