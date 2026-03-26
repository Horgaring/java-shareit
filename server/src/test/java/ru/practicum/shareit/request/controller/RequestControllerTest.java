package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private RequestService requestService;
    @InjectMocks
    private RequestController requestController;
    private RequestDto requestDto1;
    private RequestDto requestDto2;
    private Request request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController)
                .setControllerAdvice(AdviceController.class).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        requestDto1 = new RequestDto();
        requestDto1.setId(1L);
        requestDto1.setDescription("Need a power drill");
        requestDto1.setRequestor(1L);
        requestDto1.setCreated(LocalDateTime.now());
        requestDto1.setItems(Collections.emptyList());

        requestDto2 = new RequestDto();
        requestDto2.setId(2L);
        requestDto2.setDescription("Looking for a ladder");
        requestDto2.setRequestor(2L);
        requestDto2.setCreated(LocalDateTime.now().minusHours(1));
        requestDto2.setItems(Collections.emptyList());

        request = new Request();
        request.setId(1L);
        request.setDescription("Need a power drill");
        request.setRequestor(User.builder().id(1L).build());
        request.setCreated(LocalDateTime.now());
    }

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        when(requestService.createRequest(any(Request.class))).thenReturn(requestDto1);

        mockMvc.perform(post("/requests")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a power drill"))
                .andExpect(jsonPath("$.requestor").value(1L));
    }

    @Test
    void getRequests_ShouldReturnAllRequests() throws Exception {
        List<RequestDto> requestDtos = List.of(requestDto1, requestDto2);
        when(requestService.getRequests()).thenReturn(requestDtos);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need a power drill"))
                .andExpect(jsonPath("$[0].requestor").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Looking for a ladder"))
                .andExpect(jsonPath("$[1].requestor").value(2L));
    }

    @Test
    void getRequests_ShouldReturnEmptyList() throws Exception {
        when(requestService.getRequests()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUserRequests_ShouldReturnUserRequests() throws Exception {
        List<RequestDto> userRequests = List.of(requestDto1);
        when(requestService.getRequestsByUserId(anyLong())).thenReturn(userRequests);

        mockMvc.perform(get("/requests")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need a power drill"))
                .andExpect(jsonPath("$[0].requestor").value(1L));
    }

    @Test
    void getUserRequests_ShouldReturnEmptyListForUser() throws Exception {
        when(requestService.getRequestsByUserId(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header(Headers.X_SHARER_USER_ID, "999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUserRequests_WithoutHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestByIds_ShouldReturnRequest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(requestDto1);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a power drill"))
                .andExpect(jsonPath("$.requestor").value(1L));
    }

    @Test
    void getRequestByIds_WithDifferentUserId_ShouldReturnRequest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(requestDto1);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(Headers.X_SHARER_USER_ID, "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getRequestByIds_WithoutHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



    @Test
    void getRequests_WithInvalidMethod_ShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    void getUserRequests_WithInvalidMethod_ShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }
}