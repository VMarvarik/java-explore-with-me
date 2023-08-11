package ru.practicum.statclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;

import java.util.List;

@Slf4j
public class StatClient {
    private static final String STAT_SERVER_URL = System.getenv().get("STATS_SERVER_URL");
    private final WebClient webClient = WebClient.builder()
            .baseUrl(STAT_SERVER_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void addHit(EndpointHitDto endpointHitDto) {
        webClient
                .post()
                .uri("/hit")
                .bodyValue(endpointHitDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        StringBuilder urisToSend = new StringBuilder();
        for (String uri : uris) {
            urisToSend.append(uri).append(",");
        }
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", urisToSend)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                })
                .block();
    }
}
