package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @Column(name = "user_id")
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
