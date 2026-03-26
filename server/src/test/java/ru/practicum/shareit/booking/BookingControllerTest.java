package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.constants.Headers;
import ru.practicum.shareit.exception.AdviceController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

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
class BookingControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(AdviceController.class).build();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto1.setStatus(BookingStatus.WAITING);

        bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.now().plusDays(3));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(4));
        bookingDto2.setStatus(BookingStatus.APPROVED);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        when(bookingService.create(any(BookingRequestDto.class))).thenReturn(bookingDto1);

        mockMvc.perform(post("/bookings")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void updateBooking_ShouldReturnUpdatedBooking() throws Exception {
        BookingDto approvedBooking = new BookingDto();
        approvedBooking.setId(1L);
        approvedBooking.setStart(LocalDateTime.now().plusDays(1));
        approvedBooking.setEnd(LocalDateTime.now().plusDays(2));
        approvedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingService.update(eq(1L), eq(1L), eq(true))).thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/1")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_ShouldReturnBooking() throws Exception {
        when(bookingService.get(eq(1L), eq(1L))).thenReturn(bookingDto1);

        mockMvc.perform(get("/bookings/1")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getAllBookingsByUser_ShouldReturnList() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto1, bookingDto2);
        when(bookingService.getAllByUser(eq(1L), eq("ALL"))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    void getAllBookingsByUser_WithStateParameter_ShouldReturnFilteredList() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto1);
        when(bookingService.getAllByUser(eq(1L), eq("WAITING"))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnList() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto1, bookingDto2);
        when(bookingService.getAllByOwner(eq(1L), eq("ALL"))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    void getAllBookingsByOwner_WithStateParameter_ShouldReturnFilteredList() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto2);
        when(bookingService.getAllByOwner(eq(1L), eq("APPROVED"))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .param("state", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void createBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(bookingService.get(eq(1L), eq(999L)))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/999")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBooking_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(bookingService.update(eq(1L), eq(999L), eq(true)))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(patch("/bookings/999")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingsByUser_WithInvalidState_ShouldReturnBadRequest() throws Exception {
        when(bookingService.getAllByUser(eq(1L), eq("INVALID")))
                .thenThrow(new BadRequestException("Unknown state: INVALID"));

        mockMvc.perform(get("/bookings")
                        .header(Headers.X_SHARER_USER_ID, "1")
                        .param("state", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingsByUser_WithEmptyResult_ShouldReturnEmptyList() throws Exception {
        when(bookingService.getAllByUser(eq(1L), eq("ALL"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header(Headers.X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}