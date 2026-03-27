package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private Long requestId;
    private Long userId;

    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;

    public Item toItem() {
        Item item = new Item();
        item.setId(this.id);
        item.setName(this.name);
        item.setDescription(this.description);
        item.setAvailable(this.available);
        if (this.requestId != null) {
            item.setRequest(Request.builder().id(this.requestId).build());
        } else {
            item.setRequest(null);
        }
        if (userId != null) {
            item.setUser(User.builder().id(userId).build());
        } else {
            item.setUser(null);
        }
        return item;
    }
}
