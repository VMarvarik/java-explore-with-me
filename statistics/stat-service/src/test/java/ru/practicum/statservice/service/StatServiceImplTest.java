package ru.practicum.statservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.EndpointHitEntity;
import ru.practicum.statservice.StatRepository;
import ru.practicum.statservice.utils.StatRequestParams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StatServiceImplTest {
    @Mock
    private StatRepository statRepository;
    private StatService statService;

    @BeforeEach
    void setUp() {
        statService = new StatServiceImpl(statRepository);
    }

    @Test
    void testAddHit() {
        EndpointHitDto endpointHitDto = new EndpointHitDto("app", "uri", "ip", "2021-01-01 00:00:00");
        EndpointHitEntity endpointHitEntity = new EndpointHitEntity(null, "app", "uri", "ip", LocalDateTime.parse("2021-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Mockito.when(statRepository.save(Mockito.any(EndpointHitEntity.class))).thenReturn(endpointHitEntity);
        statService.addHit(endpointHitDto);
        Mockito.verify(statRepository).save(Mockito.any(EndpointHitEntity.class));
        Mockito.verifyNoMoreInteractions(statRepository);
    }

    @Test
    void testAllGetStats() {
        StatRequestParams params = new StatRequestParams(
                LocalDateTime.parse("2020-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse("2035-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of(),
                false
        );
        Mockito.when(statRepository.getAllStats(params.getStart(), params.getEnd())).thenReturn(List.of(Mockito.mock(ViewStatsDto.class)));
        statService.getStats(params);
        Mockito.verify(statRepository).getAllStats(params.getStart(), params.getEnd());
        Mockito.verifyNoMoreInteractions(statRepository);
    }

    @Test
    void testAllGetStatsByUris() {
        StatRequestParams params = new StatRequestParams(
                LocalDateTime.parse("2020-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse("2035-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of("/event"),
                false
        );
        Mockito.when(statRepository.getAllStatsInUris(params.getUris(), params.getStart(), params.getEnd()))
                .thenReturn(List.of(Mockito.mock(ViewStatsDto.class)));
        statService.getStats(params);
        Mockito.verify(statRepository).getAllStatsInUris(params.getUris(), params.getStart(), params.getEnd());
        Mockito.verifyNoMoreInteractions(statRepository);
    }

    @Test
    void testAllGetStatsByUniqueIp() {
        StatRequestParams params = new StatRequestParams(
                LocalDateTime.parse("2020-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse("2035-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of(),
                true
        );
        Mockito.when(statRepository.getAllStatsByDistinctIp(params.getStart(), params.getEnd())).thenReturn(List.of(Mockito.mock(ViewStatsDto.class)));
        statService.getStats(params);
        Mockito.verify(statRepository).getAllStatsByDistinctIp(params.getStart(), params.getEnd());
        Mockito.verifyNoMoreInteractions(statRepository);
    }

    @Test
    void testAllGetStatsByUrisAndUniqueIp() {
        StatRequestParams params = new StatRequestParams(
                LocalDateTime.parse("2020-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse("2035-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of("/event"),
                true
        );
        Mockito.when(statRepository.getAllStatsInUrisByDistinctIp(params.getUris(), params.getStart(), params.getEnd()))
                .thenReturn(List.of(Mockito.mock(ViewStatsDto.class)));
        statService.getStats(params);
        Mockito.verify(statRepository).getAllStatsInUrisByDistinctIp(params.getUris(), params.getStart(), params.getEnd());
        Mockito.verifyNoMoreInteractions(statRepository);
    }
}