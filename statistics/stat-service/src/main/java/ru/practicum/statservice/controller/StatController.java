package ru.practicum.statservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.model.StatForRequest;
import ru.practicum.statservice.service.StatService;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody EndpointHitDto hit) {
        log.info("Добавление хит: {}", hit);
        statService.addHit(hit);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(StatForRequest params) {
        log.info("Выгружается статистика с параметрами: {}", params);
        return statService.getStats(params);
    }
}
