package ru.practicum.mainservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.mainservice.entity.Location;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * DTO for {@link Location}
 */
@AllArgsConstructor
@Getter
public class LocationDto {
    @NotNull(message = "lat should be specified")
    @DecimalMin(value = "-180.0", message = "lat should be from -180.0 to 180.0")
    @DecimalMax(value = "180.0", message = "lat should be from -180.0 to 180.0")
    private final Double lat;
    @NotNull(message = "lon should be specified")
    @DecimalMin(value = "-180.0", message = "lon should be from -180.0 to 180.0")
    @DecimalMax(value = "180.0", message = "lon should be from -180.0 to 180.0")
    private final Double lon;
}