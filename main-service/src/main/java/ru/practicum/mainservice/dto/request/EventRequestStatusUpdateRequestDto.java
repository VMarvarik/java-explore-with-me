package ru.practicum.mainservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.mainservice.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Getter
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    private final RequestStatus status;
    private final List<Long> requestIds;
}
