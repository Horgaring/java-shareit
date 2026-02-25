package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository items;
    private final UserService userService;

    public ItemServiceImpl(UserService userService, ItemRepository items) {
        this.items = items;
        this.userService = userService;
    }


    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userService.getUserById(userId);
        var item = itemDto.toItem();
        item.setOwnerId(userId);
        items.save(item);
        itemDto.setId(item.getId());
        log.info("Item {} added", itemDto);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {
        var owner = getItemById(itemDto.getId()).getOwnerId();
        if (!owner.equals(userId)) {
            throw new NotFoundException("User is not the owner of the item");
        }

        var itemFromStorage = getItemById(itemDto.getId());
        if (itemDto.getName() != null) {
            itemFromStorage.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemFromStorage.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemFromStorage.setAvailable(itemDto.getAvailable());
        }

        items.save(itemFromStorage);

        return itemFromStorage.toItemDto();
    }


    @Override
    public Item getItemById(Long id) {
        return items.findById(id)
                .orElseThrow(() -> new NotFoundException("Item is not found"));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return items.findAllByOwnerId(userId).stream()
                .map(Item::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();

        return items.searchItemByNameAndDescription(text).stream()
                .map(Item::toItemDto)
                .toList();
    }

}
