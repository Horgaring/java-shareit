package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;

    public ItemDto toItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(this.id);
        itemDto.setName(this.name);
        itemDto.setDescription(this.description);
        itemDto.setAvailable(this.available);
        return itemDto;
    }
}
