package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.BadRequestException;

public final class Validators {
    private Validators() {
        // Utility class
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " must not be blank");
        }
    }

    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " must not be null");
        }
    }

    public static void validateEmail(String email) {
        validateNotNull(email, "email");
        if (!email.contains("@") || !email.contains(".")) {
            throw new BadRequestException("Invalid email format");
        }
    }
}
