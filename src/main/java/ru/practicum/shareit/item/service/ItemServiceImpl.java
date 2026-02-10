package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items;
    private final UserService userService;
    private Long idCounter = 1L;

    public ItemServiceImpl(UserService userService) {
        this.items = new HashMap<>();
        this.userService = userService;
    }

    private void validateUser(Long userId) {
        if (!userService.getUserById(userId).isPresent()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        validateUser(userId);
        var item = itemDto.toItem();
        item.setOwnerId(userId);
        items.put(idCounter, item);
        item.setId(idCounter);
        itemDto.setId(idCounter);
        idCounter++;
        log.info("Item {} added", itemDto);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {
        if (items.get(itemDto.getId()).getOwnerId() != userId) {
            throw new NotFoundException("User is not the owner of the item");
        }

        var itemFromStorage = items.get(itemDto.getId());
        if (itemDto.getName() != null) {
            itemFromStorage.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemFromStorage.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemFromStorage.setAvailable(itemDto.getAvailable());
        }

        return itemFromStorage.toItemDto();
    }

    @Override
    public ItemDto getItemById(Long id) {
        return items.get(id).toItemDto();
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(Item::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> {
                    String name = item.getName();
                    String description = item.getDescription();
                    return (name != null && name.toLowerCase().contains(lowerText)) ||
                            (description != null && description.toLowerCase().contains(lowerText));
                })
                .map(Item::toItemDto)
                .toList();
    }

}
