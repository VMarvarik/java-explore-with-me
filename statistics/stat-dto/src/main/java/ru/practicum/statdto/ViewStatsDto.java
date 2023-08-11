package ru.practicum.statdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
