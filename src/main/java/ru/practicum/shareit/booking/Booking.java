package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private Long id;
    private Long itemId;
    private Long userId;
    private LocalDate start;
    private LocalDate end;
}
