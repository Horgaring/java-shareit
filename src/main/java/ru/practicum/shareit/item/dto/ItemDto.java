package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    @Null
    private Long id;
    @NotNull
    @NotEmpty
    @NotBlank
    private String name;
    @NotNull
    @NotEmpty
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    public Item toItem() {
        Item item = new Item();
        item.setId(this.id);
        item.setName(this.name);
        item.setDescription(this.description);
        item.setAvailable(this.available);
        return item;
    }
}
