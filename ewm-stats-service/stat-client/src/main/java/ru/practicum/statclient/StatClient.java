package ru.practicum.statclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;

import java.util.List;

@Slf4j
@Component
public class StatClient {

    private final String statsServerUrl;
    private final RestTemplate restTemplate;

    public StatClient(@Value("${stats-server.url}") String statsServerUrl) {
        this.statsServerUrl = statsServerUrl;
        this.restTemplate = new RestTemplate();
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        log.trace("Sending endpointHitDto={} to stats-server.", endpointHitDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.postForEntity(statsServerUrl + "/hit", requestEntity, Void.class);
        log.trace("EndpointHitDto sent to stats-server.");
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        log.trace("Requesting Stats from stats-server with params: start={}, end={}, uris={}, unique={}.", start, end, uris, unique);
        String url = UriComponentsBuilder.fromUriString(statsServerUrl)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", String.join(",", uris))
                .queryParam("unique", unique)
                .toUriString();

        ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {
                }
        );
        return response.getBody();
    }
}
