package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;
    private Long userId;
    private Long itemId;
    private Instant start;
    private Instant end;
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
