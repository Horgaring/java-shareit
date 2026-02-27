package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.util.List;

@Slf4j
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
    public BookingDto create(BookingRequestDto dto) {
        log.info("Booking create: itemId={}, userId={}, start={}, end={}, now={}",
                dto.getItemId(), dto.getUserId(), dto.getStart(), dto.getEnd(), java.time.LocalDateTime.now());
        BookingValidator.validateBookingRequest(dto);

        var item = itemService.getItemById(dto.getItemId());
        var user = userService.getUserById(dto.getUserId());

        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new BadRequestException("Item is not available for booking");
        }

        if (item.getOwnerId().equals(dto.getUserId())) {
            throw new BadRequestException("Cannot book your own item");
        }

        var booking = new Booking();
        booking.setUser(user);
        booking.setItem(item);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        repository.save(booking);
        log.info("Booking created: id={}, itemId={}, userId={}, status={}, start={}, end={}",
                booking.getId(), booking.getItem().getId(), booking.getUser().getId(),
                booking.getStatus(), booking.getStart(), booking.getEnd());
        return booking.toBookingDto();
    }

    @Override
    public BookingDto update(Long userId, Long id, Boolean status) {
        var item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item Request does not exist"));
        if (!item.getItem().getOwnerId().equals(userId)) {
            throw new PermissionDeniedException("Not allowed");
        }
        item.setStatus(status ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        repository.save(item);
        return item.toBookingDto();
    }

    @Override
    public BookingDto get(Long id, Long bookingId) {
        var itemReq = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Item Request does not exist"));
        if (!itemReq.getUser().getId().equals(id) && !itemReq.getItem().getOwnerId().equals(id)) {
            throw new PermissionDeniedException("Not allowed");
        }

        return itemReq.toBookingDto();
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
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

        return bookings.stream().map(Booking::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, String state) {
        userService.getUserById(ownerId);
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

        return bookings.stream().map(Booking::toBookingDto).toList();
    }
}
