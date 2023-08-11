package ru.practicum.statservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statservice.service.StatService;
import ru.practicum.statservice.utils.StatRequestParams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatController.class)
class StatControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private StatService statService;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void whenPostHitThenCreated() throws Exception {
        EndpointHitDto in = new EndpointHitDto("app", "uri", "ip", "time");
        mvc.perform(
                post("/hit")
                        .content(mapper.writeValueAsString(in))
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
        ).andExpect(status().isCreated());
    }

    @Test
    void whenGetStatsThenOk() throws Exception {
        mvc.perform(
                get("/stats")
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "/event")
                        .param("unique", "true")
        ).andExpect(status().isOk());
    }

    @Test
    void whenGetStatsThenOkAndReturnDto() throws Exception {
        StatRequestParams statRequestParams = new StatRequestParams(
                LocalDateTime.parse("2020-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse("2035-05-05 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of("/event"),
                true
        );
        List<ViewStatsDto> out = List.of(new ViewStatsDto("app", "uri", 1L));
        given(statService.getStats(statRequestParams)).willReturn(out);
        mvc.perform(
                        get("/stats")
                                .param("start", "2020-05-05 00:00:00")
                                .param("end", "2035-05-05 00:00:00")
                                .param("uris", "/event")
                                .param("unique", "true")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value(out.get(0).getApp()))
                .andExpect(jsonPath("$[0].uri").value(out.get(0).getUri()))
                .andExpect(jsonPath("$[0].hits").value(out.get(0).getHits()))
        ;

        verify(statService, times(1)).getStats(statRequestParams);
    }
}