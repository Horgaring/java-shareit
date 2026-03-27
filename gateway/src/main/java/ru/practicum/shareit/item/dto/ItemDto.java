package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.Validators;

import java.util.List;

@Setter
@Getter
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private Long requestId;

    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;


    public void validateItemDto() {
        if (this.id != null) {
            throw new IllegalArgumentException("Item id must be null for creation");
        }
        Validators.validateNotBlank(this.name, "name");
        Validators.validateNotBlank(this.description, "description");
        Validators.validateNotNull(this.available, "available");
    }
}
