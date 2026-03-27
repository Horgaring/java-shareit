package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestTest {

    private User testUser;
    private Item testItem1;
    private Item testItem2;
    private LocalDateTime testCreatedTime;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        testItem1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .user(testUser)
                .build();

        testItem2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .user(testUser)
                .build();

        testCreatedTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    }

    @Test
    @DisplayName("Should create Request with builder pattern")
    void shouldCreateRequestWithBuilder() {
        Request request = Request.builder()
                .id(1L)
                .description("Need a power drill for home renovation")
                .requestor(testUser)
                .created(testCreatedTime)
                .items(List.of(testItem1, testItem2))
                .build();

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Need a power drill for home renovation");
        assertThat(request.getRequestor()).isEqualTo(testUser);
        assertThat(request.getCreated()).isEqualTo(testCreatedTime);
        assertThat(request.getItems()).containsExactly(testItem1, testItem2);
    }

    @Test
    @DisplayName("Should create Request with no-args constructor and setters")
    void shouldCreateRequestWithNoArgsConstructorAndSetters() {
        Request request = new Request();

        request.setId(1L);
        request.setDescription("Need a laptop for work");
        request.setRequestor(testUser);
        request.setCreated(testCreatedTime);
        request.setItems(List.of(testItem1));

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Need a laptop for work");
        assertThat(request.getRequestor()).isEqualTo(testUser);
        assertThat(request.getCreated()).isEqualTo(testCreatedTime);
        assertThat(request.getItems()).containsExactly(testItem1);
    }

    @Test
    @DisplayName("Should convert Request to DTO with items")
    void shouldConvertRequestToDtoWithItems() {
        Request request = Request.builder()
                .id(1L)
                .description("Need a camera for photography")
                .requestor(testUser)
                .created(testCreatedTime)
                .items(List.of(testItem1, testItem2))
                .build();

        RequestDto dto = request.toDto();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a camera for photography");
        assertThat(dto.getRequestor()).isEqualTo(testUser.getId());
        assertThat(dto.getCreated()).isEqualTo(testCreatedTime);
        assertThat(dto.getItems()).hasSize(2);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Item 1");
        assertThat(dto.getItems().get(1).getName()).isEqualTo("Item 2");
    }

    @Test
    @DisplayName("Should convert Request to DTO with null items")
    void shouldConvertRequestToDtoWithNullItems() {
        Request request = Request.builder()
                .id(1L)
                .description("Need a camera for photography")
                .requestor(testUser)
                .created(testCreatedTime)
                .items(null)
                .build();

        RequestDto dto = request.toDto();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a camera for photography");
        assertThat(dto.getRequestor()).isEqualTo(testUser.getId());
        assertThat(dto.getCreated()).isEqualTo(testCreatedTime);
        assertThat(dto.getItems()).isNull();
    }

    @Test
    @DisplayName("Should convert Request to DTO with empty items list")
    void shouldConvertRequestToDtoWithEmptyItems() {
        Request request = Request.builder()
                .id(1L)
                .description("Need a camera for photography")
                .requestor(testUser)
                .created(testCreatedTime)
                .items(List.of())
                .build();

        RequestDto dto = request.toDto();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a camera for photography");
        assertThat(dto.getRequestor()).isEqualTo(testUser.getId());
        assertThat(dto.getCreated()).isEqualTo(testCreatedTime);
        assertThat(dto.getItems()).isEmpty();
    }
}