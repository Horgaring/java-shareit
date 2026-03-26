package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository repository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private BookingServiceImpl service;

    @Test
    public void shouldThrowPermissionException_whenBookingOwnerDoesntEqualUser() {
        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(Booking.builder()
                        .item(Item.builder().user(User.builder().id(123L).build()).build()).build()));
        var userId = 1L;

        Assertions.assertThrows(PermissionDeniedException.class, () -> service.update(userId, 1L, true));
    }

    @Test
    public void shouldThrowPermissionException_whenGetBookingByWrongUserId() {
        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(Booking.builder()
                        .user(User.builder().id(555L).build())
                        .item(Item.builder().user(User.builder().id(123L).build()).build()).build()));
        var userId = 1L;

        Assertions.assertThrows(PermissionDeniedException.class, () -> service.get(userId, 18L));
    }

    @Test
    void getAllByUserShouldReturnBookingsForAllState() {
        when(repository.findByUser_IdOrderByStartDesc(any(Long.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByUser(1L, "ALL");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getAllByUserShouldReturnBookingsForCurrentState() {
        when(repository.findCurrentByUserId(any(Long.class), any(Instant.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByUser(1L, "CURRENT");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByUserShouldReturnBookingsForPastState() {
        when(repository.findByUser_IdAndEndBeforeOrderByStartDesc(any(Long.class), any(Instant.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByUser(1L, "PAST");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByUserShouldReturnBookingsForFutureState() {
        when(repository.findByUser_IdAndStartAfterOrderByStartDesc(any(Long.class), any(Instant.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByUser(1L, "FUTURE");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByUserShouldReturnBookingsForWaitingState() {
        when(repository.findByUser_IdAndStatusOrderByStartDesc(any(Long.class), any(BookingStatus.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByUser(1L, "WAITING");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByUserShouldReturnBookingsForRejectedState() {
        when(repository.findByUser_IdAndStatusOrderByStartDesc(any(Long.class), any(BookingStatus.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByUser(1L, "REJECTED");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByUserShouldReturnBookingsForNullState() {
        when(repository.findByUser_IdOrderByStartDesc(any(Long.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByUser(1L, null);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerShouldReturnBookingsForAllState() {
        when(repository.findByItemUser_IdOrderByStartDesc(any(Long.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByOwner(1L, "ALL");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getAllByOwnerShouldReturnBookingsForCurrentState() {
        when(repository.findCurrentByOwnerId(any(Long.class), any(Instant.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByOwner(1L, "CURRENT");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerShouldReturnBookingsForPastState() {
        when(repository.findByItemUser_IdAndEndBeforeOrderByStartDesc(any(Long.class), any(Instant.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByOwner(1L, "PAST");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerShouldReturnBookingsForFutureState() {
        when(repository.findByItemUser_IdAndStartAfterOrderByStartDesc(any(Long.class), any(Instant.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByOwner(1L, "FUTURE");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerShouldReturnBookingsForWaitingState() {
        when(repository.findByItemUser_IdAndStatusOrderByStartDesc(any(Long.class), any(BookingStatus.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByOwner(1L, "WAITING");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerShouldReturnBookingsForRejectedState() {
        when(repository.findByItemUser_IdAndStatusOrderByStartDesc(any(Long.class), any(BookingStatus.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByOwner(1L, "REJECTED");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerShouldReturnBookingsForNullState() {
        when(repository.findByItemUser_IdOrderByStartDesc(any(Long.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .build()));

        List<BookingDto> result = service.getAllByOwner(1L, null);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByUserShouldReturnEmptyListWhenNoBookings() {
        when(repository.findByUser_IdOrderByStartDesc(any(Long.class)))
                .thenReturn(List.of());

        List<BookingDto> result = service.getAllByUser(1L, "ALL");

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getAllByOwnerShouldReturnEmptyListWhenNoBookings() {
        when(repository.findByItemUser_IdOrderByStartDesc(any(Long.class)))
                .thenReturn(List.of());

        List<BookingDto> result = service.getAllByOwner(1L, "ALL");

        Assertions.assertTrue(result.isEmpty());
    }
}
