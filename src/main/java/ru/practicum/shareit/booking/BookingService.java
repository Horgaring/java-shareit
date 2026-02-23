package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.ItemRequestDto;

import java.util.List;

@Service
public interface BookingService {
    ItemRequestDto create(ItemRequestDto dto);
    ItemRequestDto update(Long userId, Long id, Boolean status);

    ItemRequestDto get(Long id, Long bookingId);

    List<ItemRequestDto> getAllByUser(Long userId, String state);

    List<ItemRequestDto> getAllByOwner(Long ownerId, String state);
}
