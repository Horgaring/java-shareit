package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private BookingStatus status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;

    public ItemRequestDto toItemRequest() {
        var item = new ItemRequestDto();
        item.setBookingStatus(status);
        item.setId(id);
        item.setUserId(user.getId());
        item.setStart(start);
        item.setEnd(end);
        return item;
    }
}
