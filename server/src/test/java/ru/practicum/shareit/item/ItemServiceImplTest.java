package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

@SpringBootTest
@Transactional
public class ItemServiceImplTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemService service;

    @Test
    public void getUserItems_shouldReturnUserItems() throws Exception {
        var user = User.builder()
                .name("Anna")
                .email("AnnaT@mail.ru")
                .build();

        em.persist(user);

        var item = Item.builder()
                .name("Hammer")
                .available(true)
                .user(User.builder().id(user.getId()).build())
                .build();

        em.persist(item);

        var a = service.getAllItemsByUserId(user.getId());

        Assertions.assertNotNull(a);
        Assertions.assertEquals("Hammer", a.getFirst().getName());
        Assertions.assertEquals(user.getId(), a.getFirst().getUserId());
    }
}
