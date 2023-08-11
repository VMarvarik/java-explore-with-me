package ru.practicum.mainservice.dto.request;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EventRequestStatusUpdateResponseDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
