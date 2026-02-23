package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Long userId;

        public ItemRequest toItemRequest() {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(this.id);
            itemRequest.setName(this.name);
            itemRequest.setDescription(this.description);
            itemRequest.setUserId(this.userId);
            return itemRequest;
        }
}
