package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.validation.Validators;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userStorage;

    public UserServiceImpl(UserRepository userStorage) {
        this.userStorage = userStorage;
    }


    @Override
    public User createUser(User user) {
        Validators.validateNotBlank(user.getEmail(), "email");
        if (userStorage.findUserByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateException("Duplicate exception", "User with email " + user.getEmail() + " already exists");
        }

        log.info("User {} created", user);
        userStorage.save(user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userStorage.findById(id);
    }

    @Override
    public User updateUser(User user) {
        Validators.validateNotBlank(user.getEmail(), "email");
        if (userStorage.findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        userStorage.findById(user.getId())
                .ifPresent(s -> {
                    if (user.getEmail() != null) {
                        s.setEmail(user.getEmail());
                    }

                    if (user.getName() != null) {
                        s.setName(user.getName());
                    }
                });
        userStorage.save(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteById(id);
    }
}
