package ru.practicum.statdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ViewStatsDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private Long hits;
}
