package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.BookingDtoForItem;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookingDtoForItemTest {

    private ObjectMapper objectMapper;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        startDateTime = LocalDateTime.of(2026, 1, 15, 10, 30, 0);
        endDateTime = LocalDateTime.of(2026, 4, 15, 10, 30, 0);
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = "{" +
                "\"id\": 1," +
                "\"bookerId\": 123," +
                "\"start\": \"2026-01-15T10:30:00\"," +
                "\"end\": \"2026-04-15T10:30:00\"" +
                "}";

        BookingDtoForItem requestDto = objectMapper.readValue(json, BookingDtoForItem.class);
        assertEquals(1L, requestDto.getId());
        assertEquals(123L, requestDto.getBookerId());
        assertEquals(startDateTime, requestDto.getStart());
        assertEquals(endDateTime, requestDto.getEnd());
    }
}