package ru.practicum.shareit.booking.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

    private LocalDateTime now = LocalDateTime.now();


    @Test
    void shouldThrowExceptionWhenItemIdIsNull() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(null)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validateBookingRequest(dto));
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsNull() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(null)
                .end(now.plusHours(2))
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validateBookingRequest(dto));

    }

    @Test
    @DisplayName("Should throw exception when end time is null")
    void shouldThrowExceptionWhenEndTimeIsNull() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(now.plusHours(1))
                .end(null)
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validateBookingRequest(dto));

    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsInPast() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validateBookingRequest(dto));

    }


    @Test
    void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(now.plusHours(2))
                .end(now.plusHours(1))
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validateBookingRequest(dto));

    }

    @Test
    void shouldThrowExceptionWhenEndTimeEqualsStartTime() {
        LocalDateTime startTime = now.plusHours(1);
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(startTime)
                .end(startTime)
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> BookingValidator.validateBookingRequest(dto));

    }


}