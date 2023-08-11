package ru.practicum.mainservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.dto.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for creating new {@link ru.practicum.mainservice.entity.Event} in DB
 */
@AllArgsConstructor
@Getter
public class NewEventDto {
    @Length(message = "Annotation should be between 20 and 2000 chars", min = 20, max = 2000)
    @NotBlank(message = "Annotation should not be blank")
    private final String annotation;

    @NotNull(message = "Category should not be null")
    private final Long category;

    @Length(message = "Description should be between 20 and 7000 chars", min = 20, max = 7000)
    @NotBlank(message = "Description should not be blank")
    private final String description;

    @NotNull(message = "Event date should not be null")
    @Future(message = "Event date can't be in past")
    private final LocalDateTime eventDate;

    @NotNull(message = "Location should not be null")
    private final LocationDto location;

    private final Boolean paid = false;
    private final Integer participantLimit = 0;
    private final Boolean requestModeration = true;

    @Length(message = "Title should be between 3 and 120 chars", min = 3, max = 120)
    @NotBlank(message = "Title should not be blank")
    private final String title;
}