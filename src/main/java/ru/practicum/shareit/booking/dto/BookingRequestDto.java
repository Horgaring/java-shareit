package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    private Long id;
    private Long userId;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus bookingStatus;


        public Booking toItemRequest() {
            Booking itemRequest = new Booking();
            itemRequest.setId(this.id);
            itemRequest.setUser(new User(userId, null, null));
            itemRequest.setItem(new Item(itemId, null, null, null,null));
            itemRequest.setStart(start);
            itemRequest.setEnd(end);
            itemRequest.setStatus(bookingStatus);
            return itemRequest;
        }
}
