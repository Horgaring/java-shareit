package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidator {

    public static void validateBookingRequest(BookItemRequestDto dto) {
        if (dto.getStart() == null) {
            throw new ValidationException("Start time cannot be null");
        }
        if (dto.getEnd() == null) {
            throw new ValidationException("End time cannot be null");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start time must be in the present or future");
        }
        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new ValidationException("End time must be after start time");
        }
        if (dto.getEnd().isEqual(dto.getStart())) {
            throw new ValidationException("End time must not be equal start time");
        }
    }
}
