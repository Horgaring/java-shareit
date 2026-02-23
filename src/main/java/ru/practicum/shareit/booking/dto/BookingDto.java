package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class BookingDto {
    private Long id;
    private Long itemId;
    private Long userId;
    private LocalDate start;
    private LocalDate end;

    public Booking toBooking() {
        Booking booking = new Booking();
        booking.setId(this.id);
        booking.setItemId(this.itemId);
        booking.setUserId(this.userId);
        booking.setStart(this.start);
        booking.setEnd(this.end);
        return booking;
    }
}
