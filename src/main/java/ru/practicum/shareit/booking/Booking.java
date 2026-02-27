package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
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
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;

    public BookingRequestDto toItemRequest() {
        var item = new BookingRequestDto();
        item.setBookingStatus(status);
        item.setId(id);
        item.setUserId(user.getId());
        item.setStart(start);
        item.setEnd(end);
        return item;
    }

    public BookingDto toBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setStatus(status);

        if (user != null) {
            BookingDto.BookerDto booker = new BookingDto.BookerDto();
            booker.setId(user.getId());
            dto.setBooker(booker);
        }

        if (item != null) {
            BookingDto.ItemBookingDto itemDto = new BookingDto.ItemBookingDto();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            dto.setItem(itemDto);
        }

        return dto;
    }
}
