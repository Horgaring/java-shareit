package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Headers;
import ru.practicum.shareit.request.dto.RequestDto;


@AllArgsConstructor
@RestController
@RequestMapping("/requests")
public class RequestController {
   private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequests(@RequestBody RequestDto request,
                                                @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return requestClient.createRequest(request, id);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getRequests() {
        return requestClient.getAllRequests();
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getUserRequests(@RequestHeader(value = Headers.X_SHARER_USER_ID) Long id,
                                                  @PathVariable Long requestId) {
        return requestClient.getUserRequestById(id, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return requestClient.getUserRequests(id);
    }







}
