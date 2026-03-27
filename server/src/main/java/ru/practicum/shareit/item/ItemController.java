package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Headers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("")
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        itemDto.setUserId(userId);
        return itemService.addItem(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @RequestHeader(Headers.X_SHARER_USER_ID) Long userId,
                          @PathVariable Long itemId) {
        itemDto.setId(itemId);
        itemDto.setUserId(userId);
        return itemService.updateItem(itemDto);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getitem(@PathVariable Long itemId,
                           @RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        return itemService.getItemByIdWithComments(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader(Headers.X_SHARER_USER_ID) Long userId) {
        return itemService.addComment(itemId, commentDto, userId);
    }
}
