package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestDtoTest {

    private ObjectMapper objectMapper;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        testDateTime = LocalDateTime.of(2026, 1, 15, 10, 30, 0);
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = "{" +
                "\"id\": 1," +
                "\"description\": \"Need a laptop for work\"," +
                "\"requestor\": 123," +
                "\"created\": \"2026-01-15T10:30:00\"," +
                "\"items\": [" +
                "    {" +
                "        \"id\": 101," +
                "        \"name\": \"MacBook Pro\"," +
                "        \"description\": \"Apple laptop 16GB RAM\"," +
                "        \"available\": true," +
                "        \"userId\": 456" +
                "    }" +
                "]" +
                "}";

        RequestDto requestDto = objectMapper.readValue(json, RequestDto.class);

        assertEquals(1L, requestDto.getId());
        assertEquals("Need a laptop for work", requestDto.getDescription());
        assertEquals(123L, requestDto.getRequestor());
        assertEquals(testDateTime, requestDto.getCreated());
        assertNotNull(requestDto.getItems());
        assertEquals(1, requestDto.getItems().size());

        ItemDto item = requestDto.getItems().get(0);
        assertEquals(101L, item.getId());
        assertEquals("MacBook Pro", item.getName());
        assertEquals("Apple laptop 16GB RAM", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(456L, item.getUserId());
    }

    @Test
    void testDeserializationWithoutOptionalFields() throws JsonProcessingException {
        String json = "{" +
                "\"description\": \"Simple request without ID\"," +
                "\"requestor\": 123" +
                "}";

        RequestDto requestDto = objectMapper.readValue(json, RequestDto.class);

        assertNull(requestDto.getId());
        assertEquals("Simple request without ID", requestDto.getDescription());
        assertEquals(123L, requestDto.getRequestor());
        assertNull(requestDto.getCreated());
        assertNull(requestDto.getItems());
    }

    @Test
    void testToRequest() {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Need a camera")
                .requestor(123L)
                .created(testDateTime)
                .items(List.of(
                        ItemDto.builder()
                                .id(101L)
                                .name("Canon DSLR")
                                .description("Professional camera")
                                .available(true)
                                .userId(456L)
                                .build()
                ))
                .build();

        Request request = requestDto.toRequest();

        assertEquals(1L, request.getId());
        assertEquals("Need a camera", request.getDescription());
        assertNotNull(request.getRequestor());
        assertEquals(123L, request.getRequestor().getId());
        assertEquals(testDateTime, request.getCreated());
        assertNotNull(request.getItems());
        assertEquals(1, request.getItems().size());
        assertEquals("Canon DSLR", request.getItems().get(0).getName());
    }

    @Test
    void testToRequestWithNullItems() {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Request without items")
                .requestor(123L)
                .created(testDateTime)
                .items(null)
                .build();

        Request request = requestDto.toRequest();

        assertEquals(1L, request.getId());
        assertEquals("Request without items", request.getDescription());
        assertNotNull(request.getRequestor());
        assertEquals(123L, request.getRequestor().getId());
        assertEquals(testDateTime, request.getCreated());
        assertNull(request.getItems());
    }

    @Test
    void testToRequestWithEmptyItemsList() {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Request with empty items list")
                .requestor(123L)
                .created(testDateTime)
                .items(List.of())
                .build();

        Request request = requestDto.toRequest();

        assertEquals(1L, request.getId());
        assertEquals("Request with empty items list", request.getDescription());
        assertNotNull(request.getRequestor());
        assertEquals(123L, request.getRequestor().getId());
        assertEquals(testDateTime, request.getCreated());
        assertNotNull(request.getItems());
        assertTrue(request.getItems().isEmpty());
    }

    @Test
    void testBuilderAndGetters() {
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Test description")
                .requestor(123L)
                .created(testDateTime)
                .items(List.of())
                .build();

        assertEquals(1L, requestDto.getId());
        assertEquals("Test description", requestDto.getDescription());
        assertEquals(123L, requestDto.getRequestor());
        assertEquals(testDateTime, requestDto.getCreated());
        assertNotNull(requestDto.getItems());
        assertTrue(requestDto.getItems().isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        RequestDto requestDto = new RequestDto();

        assertNull(requestDto.getId());
        assertNull(requestDto.getDescription());
        assertNull(requestDto.getRequestor());
        assertNull(requestDto.getCreated());
        assertNull(requestDto.getItems());
    }

    @Test
    void testAllArgsConstructor() {
        List<ItemDto> items = List.of(
                ItemDto.builder().id(1L).name("Item1").build(),
                ItemDto.builder().id(2L).name("Item2").build()
        );

        RequestDto requestDto = new RequestDto(1L, "Test", 123L, testDateTime, items);

        assertEquals(1L, requestDto.getId());
        assertEquals("Test", requestDto.getDescription());
        assertEquals(123L, requestDto.getRequestor());
        assertEquals(testDateTime, requestDto.getCreated());
        assertEquals(2, requestDto.getItems().size());
    }

    @Test
    void testEqualsAndHashCode() {
        RequestDto requestDto1 = RequestDto.builder()
                .id(1L)
                .description("Test")
                .requestor(123L)
                .created(testDateTime)
                .build();

        RequestDto requestDto2 = RequestDto.builder()
                .id(1L)
                .description("Test")
                .requestor(123L)
                .created(testDateTime)
                .build();

        RequestDto requestDto3 = RequestDto.builder()
                .id(2L)
                .description("Different")
                .requestor(456L)
                .created(testDateTime.plusDays(1))
                .build();

        assertEquals(requestDto1, requestDto2);
        assertEquals(requestDto1.hashCode(), requestDto2.hashCode());
        assertNotEquals(requestDto1, requestDto3);
        assertNotEquals(requestDto1.hashCode(), requestDto3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Test description")
                .requestor(123L)
                .created(testDateTime)
                .build();

        // Act
        String toString = requestDto.toString();

        // Assert
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("description=Test description");
        assertThat(toString).contains("requestor=123");
        assertThat(toString).contains("created=" + testDateTime);
    }
}