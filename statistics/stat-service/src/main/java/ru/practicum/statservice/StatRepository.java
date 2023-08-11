package ru.practicum.statservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.model.EndpointHitEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query("select new ru.practicum.statdto.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
            "from EndpointHitEntity e " +
            "where e.uri in ?1 and e.created between ?2 and ?3 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> getAllStatsInUris(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.statdto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHitEntity e " +
            "where e.uri in ?1 and e.created between ?2 and ?3 " +
            "group by e.app, e.uri " +
            "order by count(distinct e.ip) desc")
    List<ViewStatsDto> getAllStatsInUrisByDistinctIp(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.statdto.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
            "from EndpointHitEntity e " +
            "where e.created between ?1 and ?2 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.statdto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHitEntity e " +
            "where e.created between ?1 and ?2 " +
            "group by e.app, e.uri " +
            "order by count(distinct e.ip) desc")
    List<ViewStatsDto> getAllStatsByDistinctIp(LocalDateTime start, LocalDateTime end);
}
