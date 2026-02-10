package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserService {
    User createUser(User user);

    Optional<User> getUserById(Long id);

    User updateUser(User user);

    void deleteUser(Long id);
}
