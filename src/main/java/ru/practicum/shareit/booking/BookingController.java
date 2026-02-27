package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.constants.Headers;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(@RequestBody BookingRequestDto dto,
                             @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        dto.setBookingStatus(BookingStatus.WAITING);
        dto.setUserId(id);
        return service.create(dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                                 @RequestParam Boolean approved,
                                 @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.update(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Long bookingId,
                              @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.get(id, bookingId);
    }

    @GetMapping
    public java.util.List<BookingDto> getAllByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                       @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.getAllByUser(id, state);
    }

    @GetMapping("/owner")
    public java.util.List<BookingDto> getAllByOwner(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.getAllByOwner(id, state);
    }
}
