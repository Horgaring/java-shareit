package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Headers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemClient itemService;


    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ItemDto itemDto,
                                         @RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        itemDto.validateItemDto();
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @RequestHeader(Headers.X_SHARER_USER_ID) Long userId,
                                         @PathVariable Long itemId) {
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getitem(@PathVariable Long itemId,
                                          @RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        return itemService.getItemByIdWithComments(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto,
                                             @RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        return itemService.addComment(itemId, commentDto, userId);
    }
}
