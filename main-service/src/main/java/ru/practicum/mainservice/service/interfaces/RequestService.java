package ru.practicum.mainservice.service.interfaces;

import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateResponseDto;
import ru.practicum.mainservice.dto.request.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getAllUserRequests(Long userId);

    List<RequestDto> getAllUserEventRequests(Long eventId, Long userId);

    EventRequestStatusUpdateResponseDto updateRequestsStatus(EventRequestStatusUpdateRequestDto updater,
                                                             Long eventId, Long userId);
}
