package ru.practicum.statdto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class EndpointHitDto {
    @NotBlank(message = "app is mandatory")
    private String app;
    @NotBlank(message = "uri is mandatory")
    private String uri;
    @NotBlank(message = "ip is mandatory")
    private String ip;
    @NotBlank(message = "timestamp is mandatory")
    private String timestamp;
}
