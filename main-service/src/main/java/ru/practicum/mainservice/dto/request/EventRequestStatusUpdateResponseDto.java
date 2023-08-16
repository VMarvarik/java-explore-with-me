package ru.practicum.mainservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EventRequestStatusUpdateResponseDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
