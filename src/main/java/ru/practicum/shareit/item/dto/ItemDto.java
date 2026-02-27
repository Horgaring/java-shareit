package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.Validators;

import java.util.List;

@Getter
@Setter
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;

    public Item toItem() {
        validateItemDto();
        Item item = new Item();
        item.setId(this.id);
        item.setName(this.name);
        item.setDescription(this.description);
        item.setAvailable(this.available);
        return item;
    }

    public void validateItemDto() {
        if (this.id != null) {
            throw new IllegalArgumentException("Item id must be null for creation");
        }
        Validators.validateNotBlank(this.name, "name");
        Validators.validateNotBlank(this.description, "description");
        Validators.validateNotNull(this.available, "available");
    }
}
