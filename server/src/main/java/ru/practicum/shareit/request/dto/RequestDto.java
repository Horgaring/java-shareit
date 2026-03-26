package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemDto> items;

    public Request toRequest() {
        return Request.builder()
                .id(this.id)
                .description(this.description)
                .requestor(User.builder().id(this.requestor).build())
                .created(this.created)
                .items(this.items == null ? null : this.items.stream().map(ItemDto::toItem).toList())
                .build();
    }
}
