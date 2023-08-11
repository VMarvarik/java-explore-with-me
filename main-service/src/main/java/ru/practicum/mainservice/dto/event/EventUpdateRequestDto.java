package ru.practicum.mainservice.dto.event;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.model.enums.StateAction;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@ToString
@Builder
public class EventUpdateRequestDto {
    @Length(message = "Размер аннотации от 20 до 2000 знаков", min = 20, max = 2000)
    private final String annotation;
    private final Long category;
    @Length(message = "Размер описания от 20 до 7000 знаков", min = 20, max = 7000)
    private final String description;
    @Future(message = "Дата события должна быть в будущем")
    private final LocalDateTime eventDate;
    private final Long initiator;
    private final LocationDto location;
    private final Boolean paid;
    private final Integer participantLimit;
    private final Boolean requestModeration;
    private final StateAction stateAction;
    @Length(message = "Размер названия от 3 до 120 символов", min = 3, max = 120)
    private final String title;
}
