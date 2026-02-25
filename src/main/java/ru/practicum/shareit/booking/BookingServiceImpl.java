package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.booking.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(BookingRepository repository, UserService userService, ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto dto) {
        itemService.getItemById(dto.getItemId());
        userService.getUserById(dto.getUserId());
        var item = dto.toItemRequest();
        repository.save(item);
        dto.setId(item.getId());
        return dto;
    }

    @Override
    public ItemRequestDto update(Long userId, Long id, Boolean status) {
        var item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item Request does not exist"));
        if (!item.getItem().getOwnerId().equals(userId)) {
            throw new PermissionDeniedException("Not allowed");
        }
        item.setStatus(status ? BookingStatus.ACCEPTED : BookingStatus.REJECTED);
        repository.save(item);
        return item.toItemRequest();
    }

    @Override
    public ItemRequestDto get(Long id, Long bookingId) {
        var itemReq = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Item Request does not exist"));
        if (!itemReq.getUser().getId().equals(id) && !itemReq.getItem().getOwnerId().equals(id)) {
            throw new PermissionDeniedException("Not allowed");
        }

        return itemReq.toItemRequest();
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId, String state) {
        BookingState bookingState = state == null ? BookingState.ALL : BookingState.valueOf(state);
        Instant now = Instant.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> repository.findByUser_IdOrderByStartDesc(userId);
            case CURRENT -> repository.findCurrentByUserId(userId, now);
            case PAST -> repository.findByUser_IdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> repository.findByUser_IdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> repository.findByUser_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> repository.findByUser_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream().map(Booking::toItemRequest).toList();
    }

    @Override
    public List<ItemRequestDto> getAllByOwner(Long ownerId, String state) {
        BookingState bookingState = state == null ? BookingState.ALL : BookingState.valueOf(state);
        Instant now = Instant.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> repository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT -> repository.findCurrentByOwnerId(ownerId, now);
            case PAST -> repository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> repository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING -> repository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> repository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        };

        return bookings.stream().map(Booking::toItemRequest).toList();
    }
}
