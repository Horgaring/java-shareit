package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.constants.Headers;
import ru.practicum.shareit.exception.AdviceController;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(AdviceController.class).build();
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Drill");
        itemDto1.setDescription("Powerful cordless drill");
        itemDto1.setAvailable(true);

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Hammer");
        itemDto2.setDescription("Heavy duty hammer");
        itemDto2.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great tool!");
        commentDto.setAuthorName("John Doe");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        when(itemService.addItem(any(ItemDto.class))).thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful cordless drill"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Drill");
        updatedItem.setDescription("Updated description");
        updatedItem.setAvailable(false);

        when(itemService.updateItem(any(ItemDto.class))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Drill"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        when(itemService.getItemByIdWithComments(eq(1L), eq(1L))).thenReturn(itemDto1);

        mockMvc.perform(get("/items/1")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful cordless drill"));
    }

    @Test
    void getAllItemsByUserId_ShouldReturnList() throws Exception {
        List<ItemDto> items = List.of(itemDto1, itemDto2);
        when(itemService.getAllItemsByUserId(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Hammer"));
    }

    @Test
    void searchItems_ShouldReturnList() throws Exception {
        List<ItemDto> items = List.of(itemDto1);
        when(itemService.searchItems("drill")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void searchItems_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchItems("")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addComment_ShouldReturnComment() throws Exception {
        when(itemService.addComment(eq(1L), any(CommentDto.class), eq(1L))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great tool!"))
                .andExpect(jsonPath("$.authorName").value("John Doe"));
    }

    @Test
    void createItem_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemByIdWithComments(eq(999L), eq(1L)))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isNotFound());
    }
}