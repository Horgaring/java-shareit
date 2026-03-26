package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.validation.Validators;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {
    @Test
    public void shouldReturnBadRequestExceptionWhenEmailIsNull() {
        assertThrows(BadRequestException.class, () -> Validators.validateEmail(null));
    }

    @Test
    public void shouldReturnBadRequestExceptionWhenEmailIncorrect() {
        assertThrows(BadRequestException.class, () -> Validators.validateEmail("wrongemail.com"));
    }


}
