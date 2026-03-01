package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository items;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(UserService userService, ItemRepository items,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.items = items;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }


    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        var user = userService.getUserById(userId);
        var item = itemDto.toItem();
        item.setUser(user);
        items.save(item);
        itemDto.setId(item.getId());
        log.info("Item {} added", itemDto);

        ItemDto result = item.toItemDto();
        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                .map(Comment::toCommentDto)
                .collect(Collectors.toList());
        result.setComments(comments);
        return result;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {
        var owner = getItemById(itemDto.getId()).getUser().getId();
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

        ItemDto resultDto = itemFromStorage.toItemDto();
        List<CommentDto> comments = commentRepository.findAllByItemId(itemFromStorage.getId()).stream()
                .map(Comment::toCommentDto)
                .collect(Collectors.toList());
        resultDto.setComments(comments);
        return resultDto;
    }


    @Override
    public Item getItemById(Long id) {
        return items.findById(id)
                .orElseThrow(() -> new NotFoundException("Item is not found"));
    }

    @Override
    public ItemDto getItemByIdWithComments(Long id, Long userId) {
        Item item = getItemById(id);
        ItemDto itemDto = item.toItemDto();

        List<CommentDto> comments = commentRepository.findAllByItemId(id).stream()
                .map(Comment::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);

        if (item.getUser().getId().equals(userId)) {
            setBookingsForItem(itemDto, id);
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return items.findAllByUser_Id(userId).stream()
                .map(item -> {
                    ItemDto itemDto = item.toItemDto();
                    List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                            .map(Comment::toCommentDto)
                            .collect(Collectors.toList());
                    itemDto.setComments(comments);
                    setBookingsForItem(itemDto, item.getId());
                    return itemDto;
                })
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

    private void setBookingsForItem(ItemDto itemDto, Long itemId) {
        List<Booking> lastBookings = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now());
        if (!lastBookings.isEmpty()) {
            Booking lastBooking = lastBookings.get(0);
            BookingDtoForItem lastBookingDto = new BookingDtoForItem();
            lastBookingDto.setId(lastBooking.getId());
            lastBookingDto.setBookerId(lastBooking.getUser().getId());
            lastBookingDto.setStart(lastBooking.getStart());
            lastBookingDto.setEnd(lastBooking.getEnd());
            itemDto.setLastBooking(lastBookingDto);
        }

        List<Booking> nextBookings = bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            Booking nextBooking = nextBookings.get(0);
            BookingDtoForItem nextBookingDto = new BookingDtoForItem();
            nextBookingDto.setId(nextBooking.getId());
            nextBookingDto.setBookerId(nextBooking.getUser().getId());
            nextBookingDto.setStart(nextBooking.getStart());
            nextBookingDto.setEnd(nextBooking.getEnd());
            itemDto.setNextBooking(nextBookingDto);
        }
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        var item = getItemById(itemId);
        var user = userService.getUserById(userId);

        var bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        log.info("addComment: looking for itemId={}, userId={}, now={}, bookings found={}", itemId, userId, LocalDateTime.now(), bookings.size());
        if (bookings.isEmpty()) {
            throw new BadRequestException("User can only comment after booking the item");
        }

        var comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);
        return comment.toCommentDto();
    }

}
