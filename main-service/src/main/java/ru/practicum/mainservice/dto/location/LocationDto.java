package ru.practicum.mainservice.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull(message = "Широта не может быть пустой или отсутствовать")
    @DecimalMin(value = "-180.0", message = "Широта не может быть меньше -180.0")
    @DecimalMax(value = "180.0", message = "Широта не может быть больше 180.0")
    private Double lat;

    @NotNull(message = "Долгота не может быть пустой или отсутствовать")
    @DecimalMin(value = "-180.0", message = "Долгота не может быть меньше -180.0")
    @DecimalMax(value = "180.0", message = "Долгота не может быть больше 180.0")
    private Double lon;
}
