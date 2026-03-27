package ru.practicum.shareit.user;

public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    void deleteUser(Long id);

}
