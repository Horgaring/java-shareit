package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DuplicateException;

@SpringBootTest
@Transactional
public class UserServiceImplTest {
    @Autowired
    private UserService service;

    @Test
    public void createUser_shouldReturnUser() throws Exception {
        var user = User.builder()
                .name("Anna")
                .email("AnnaT@mail.ru")
                .build();


        var a = service.createUser(user);

        Assertions.assertNotNull(a);
        Assertions.assertEquals(user.getId(), a.getId());
    }

    @Test
    public void createUsersWithSameEmails_shouldReturnDuplicateException() throws Exception {
        var user = User.builder()
                .name("Anna")
                .email("AnnaT@mail.ru")
                .build();

        var user2 = User.builder()
                .name("Annaaa")
                .email("AnnaT@mail.ru")
                .build();
        var a = service.createUser(user);

        Assertions.assertNotNull(a);
        Assertions.assertThrows(DuplicateException.class, () -> service.createUser(user2));
        Assertions.assertEquals(user.getId(), a.getId());
    }
}
