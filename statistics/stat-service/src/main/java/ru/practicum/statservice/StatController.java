package ru.practicum.statservice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.service.StatService;
import ru.practicum.statservice.utils.StatRequestParams;

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
        log.info("Registering Endpoint Hit: {}", hit);
        statService.addHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(StatRequestParams params) {
        log.info("Getting Stats with parameters: {}", params);
        return statService.getStats(params);
    }
}
