package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Headers;
import ru.practicum.shareit.booking.dto.ItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto dto) {
        dto.setBookingStatus(BookingStatus.WAITING);
        return service.create(dto);
    }

    @PatchMapping("/{bookingId}")
    public ItemRequestDto update(@PathVariable Long bookingId,
                                 @RequestParam Boolean approved,
                                 @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.update(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ItemRequestDto get(@PathVariable Long bookingId,
                              @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.get(id, bookingId);
    }

    @GetMapping
    public java.util.List<ItemRequestDto> getAllByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                       @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.getAllByUser(id, state);
    }

    @GetMapping("/owner")
    public java.util.List<ItemRequestDto> getAllByOwner(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestHeader(value = Headers.X_SHARER_USER_ID) Long id) {
        return service.getAllByOwner(id, state);
    }
}
