package ru.practicum.mainservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.dto.LocationDto;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@ToString
public class UpdateEventAdminRequestDto {
    @Length(message = "Annotation should be between 20 and 2000 chars", min = 20, max = 2000)
    private final String annotation;
    private final Long category;
    @Length(message = "Description should be between 20 and 7000 chars", min = 20, max = 7000)
    private final String description;
    @Future(message = "Event date should be in the future")
    private final LocalDateTime eventDate;
    private final LocationDto location;
    private final Boolean paid;
    private final Integer participantLimit;
    private final Boolean requestModeration;
    private final StateInAdminUpd stateAction;
    @Length(message = "Title should be between 3 and 120 chars", min = 3, max = 120)
    private final String title;

    public enum StateInAdminUpd {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
