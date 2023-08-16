package ru.practicum.mainservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.dto.location.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@ToString
@Builder
public class NewEventDto {
    @Length(message = "Размер аннотации от 20 до 2000 знаков", min = 20, max = 2000)
    @NotBlank(message = "Аннотация не может отсутствовать")
    private final String annotation;

    @NotNull(message = "Категория не может отсутствовать")
    private final Long category;

    @Length(message = "Размер описания от 20 до 7000 знаков", min = 20, max = 7000)
    @NotBlank(message = "Описание не может отсутствовать")
    private final String description;

    @NotNull(message = "Дата события не может отсутствовать")
    @Future(message = "Дата события должна быть в будущем")
    private final LocalDateTime eventDate;

    @NotNull(message = "Локация не может отсутствовать")
    private final LocationDto location;

    private final Boolean paid = false;
    private final Integer participantLimit = 0;
    private final Boolean requestModeration = true;

    @Length(message = "Размер названия от 3 до 120 символов", min = 3, max = 120)
    @NotBlank(message = "Название не может быть пустым или отсутствовать")
    private final String title;
}
