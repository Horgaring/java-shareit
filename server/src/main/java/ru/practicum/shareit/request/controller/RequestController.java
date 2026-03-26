package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Headers;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public RequestDto createRequest(@RequestBody Request request,
                                    @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        request.setRequestor(User.builder().id(id).build());
        return requestService.createRequest(request);
    }


    @GetMapping("/all")
    public List<RequestDto> getRequests() {
        return requestService.getRequests();
    }


    @GetMapping
    public List<RequestDto> getUserRequests(@RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return requestService.getRequestsByUserId(id);
    }



    @GetMapping("/{requestId}")
    public RequestDto getRequestByIds(@PathVariable Long requestId,
                           @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return requestService.getRequestById(id, requestId);
    }
}
