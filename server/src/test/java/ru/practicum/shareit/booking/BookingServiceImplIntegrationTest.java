package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private BookingService service;

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
                .name("TestItem")
                .available(true)
                .user(user)
                .build();
        em.persist(item);
        em.flush();
        itemId = item.getId();

        em.clear();
    }


    @Test
    public void createBooking_shouldReturnBookingDto() throws Exception {
        var booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .user(User.builder().id(userId).build())
                .item(Item.builder().id(itemId).build())
                .build();
        var dto = booking.toBookingRequestDto();

        var a = service.create(dto);

        Assertions.assertNotNull(a);
        Assertions.assertEquals(dto.toItemRequest().toBookingDto().getStart(), a.getStart());
        Assertions.assertEquals(userId, a.getBooker().getId());
    }

    @Test
    public void getAllByOwner_shouldReturnUserBookings() throws Exception {
        var booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .user(User.builder().id(userId).build())
                .item(Item.builder().id(itemId).build())
                .build();
        em.persist(booking);

        var a = service.getAllByUser(userId, BookingState.ALL.toString());

        Assertions.assertNotNull(a);
        Assertions.assertEquals(1, a.size());
        Assertions.assertEquals(booking.getId(), a.getFirst().getId());
    }


}
