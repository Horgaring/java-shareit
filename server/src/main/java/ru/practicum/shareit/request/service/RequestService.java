package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Request request);

    RequestDto getRequestById(Long userId, Long id);

    void deleteRequest(Long id);

    List<RequestDto> getRequests();

    List<RequestDto> getRequestsByUserId(Long id);
}
