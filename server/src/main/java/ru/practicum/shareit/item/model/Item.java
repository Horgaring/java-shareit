package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;

@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @JoinColumn(name = "request_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ItemDto toItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(this.id);
        itemDto.setName(this.name);
        itemDto.setDescription(this.description);
        itemDto.setAvailable(this.available);
        if (this.request != null) {
        itemDto.setRequestId(this.request.getId());
    }
        return itemDto;
}
}
