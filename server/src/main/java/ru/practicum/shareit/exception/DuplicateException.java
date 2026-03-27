package ru.practicum.shareit.exception;

public class DuplicateException extends RuntimeException {
    private String error;

    public DuplicateException(String error, String message) {
        super(message);
        this.error = error;
    }
}
