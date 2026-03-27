package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
public class ItemServiceImpIntlTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemService service;

    @Autowired
    private BookingService bookingService;

    private Long userId;
    private Long itemId;

    @BeforeEach
    public void setUp() {
        var user = User.builder()
                .name("TestUser")
                .email("test" + System.currentTimeMillis() + "@test.com")
                .build();
        em.persist(user);
        em.flush();
        userId = user.getId();

        var item = Item.builder()
                .name("Hammer")
                .description("A heavy hammer for construction")
                .available(true)
                .user(user)
                .build();
        em.persist(item);
        em.flush();
        itemId = item.getId();

        em.clear();
    }

    @Test
    public void getUserItems_shouldReturnUserItems() throws Exception {
        var a = service.getAllItemsByUserId(userId);

        Assertions.assertNotNull(a);
        Assertions.assertEquals("Hammer", a.getFirst().getName());
        Assertions.assertEquals(userId, a.getFirst().getUserId());
    }

    @Test
    public void updateItem_shouldUpdateNameSuccessfully() {
        ItemDto updateDto = ItemDto.builder()
                .id(itemId)
                .userId(userId)
                .name("Updated Hammer")
                .build();

        ItemDto updatedItem = service.updateItem(updateDto);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals("Updated Hammer", updatedItem.getName());
        Assertions.assertEquals("A heavy hammer for construction", updatedItem.getDescription());
        Assertions.assertTrue(updatedItem.getAvailable());
        Assertions.assertEquals(itemId, updatedItem.getId());
    }

    @Test
    public void updateItem_shouldUpdateDescriptionSuccessfully() {
        ItemDto updateDto = ItemDto.builder()
                .id(itemId)
                .userId(userId)
                .description("A lightweight hammer for household use")
                .build();

        ItemDto updatedItem = service.updateItem(updateDto);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals("Hammer", updatedItem.getName());
        Assertions.assertEquals("A lightweight hammer for household use", updatedItem.getDescription());
        Assertions.assertTrue(updatedItem.getAvailable());
        Assertions.assertEquals(itemId, updatedItem.getId());
    }

    @Test
    public void updateItem_shouldUpdateAvailabilitySuccessfully() {
        ItemDto updateDto = ItemDto.builder()
                .id(itemId)
                .userId(userId)
                .available(false)
                .build();

        ItemDto updatedItem = service.updateItem(updateDto);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals("Hammer", updatedItem.getName());
        Assertions.assertEquals("A heavy hammer for construction", updatedItem.getDescription());
        Assertions.assertFalse(updatedItem.getAvailable());
        Assertions.assertEquals(itemId, updatedItem.getId());
    }

    @Test
    public void getItemByIdWithComments_shouldReturnNextBooking() {
        var booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .user(User.builder().id(userId).build())
                .item(Item.builder().id(itemId).build()).build();
        var booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(2).plusHours(1))
                .end(LocalDateTime.now().plusDays(5))
                .item(Item.builder().id(itemId).build())
                .user(User.builder().id(userId).build()).build();

        var bookingDto = bookingService.create(booking.toBookingRequestDto());
        var bookingDto2 = bookingService.create(booking2.toBookingRequestDto());
        em.flush();
        bookingService.update(userId, bookingDto.getId(), true);
        bookingService.update(userId, bookingDto2.getId(), true);

        ItemDto updatedItem = service.getItemByIdWithComments(itemId, userId);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals(bookingDto.getId(), updatedItem.getNextBooking().getId());
    }

    @Test
    public void shouldThrowExceptionIfBookingIsNotFound() {

        Assertions.assertThrows(BadRequestException.class, () -> service.addComment(itemId, null, userId));
    }
}