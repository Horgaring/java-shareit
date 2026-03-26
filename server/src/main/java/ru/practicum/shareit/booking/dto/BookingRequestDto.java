package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
            itemRequest.setItem(new Item(itemId, null, null, null, null, new User(userId, null, null)));
            itemRequest.setStart(start);
            itemRequest.setEnd(end);
            itemRequest.setStatus(bookingStatus);
            return itemRequest;
        }
}
