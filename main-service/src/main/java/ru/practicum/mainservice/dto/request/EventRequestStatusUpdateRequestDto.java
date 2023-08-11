package ru.practicum.mainservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.practicum.mainservice.model.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    private final RequestStatus status;
    private final List<Long> requestIds;
}
