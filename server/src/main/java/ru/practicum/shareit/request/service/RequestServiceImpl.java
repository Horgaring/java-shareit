package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestStorage;



    @Override
    public RequestDto createRequest(Request request) {
        return requestStorage.save(request).toDto();
    }

    @Override
    public RequestDto getRequestById(Long userId, Long id) {
        return requestStorage.findById(id).orElseThrow(() -> new NotFoundException("Request not found")).toDto();
    }

    @Override
    public void deleteRequest(Long id) {
        requestStorage.deleteById(id);
    }

    @Override
    public List<RequestDto> getRequests() {
        return requestStorage.findAll().stream().map(Request::toDto).toList();
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long id) {
        return requestStorage.findUserRequests(id).stream().map(Request::toDto).toList();
    }
}
