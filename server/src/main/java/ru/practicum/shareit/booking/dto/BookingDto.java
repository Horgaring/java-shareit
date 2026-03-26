package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerDto booker;
    private ItemBookingDto item;

    @Data
    public static class BookerDto {
        private Long id;
    }

    @Data
    public static class ItemBookingDto {
        private Long id;
        private String name;
    }
}
