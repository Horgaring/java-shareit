package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private Map<Long, User> userStorage;
    private Long idCounter = 1L;


    public UserServiceImpl() {
        this.userStorage = new HashMap<>();
    }

    @Override
    public User createUser(User user) {
        if (userStorage.values().stream()
                .anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
            throw new DuplicateException("Duplicate exception", "User with email " + user.getEmail() + " already exists");
        }

        log.info("User {} created", user);
        user.setId(idCounter);
        userStorage.put(idCounter, user);
        idCounter++;
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userStorage.get(id));
    }

    @Override
    public User updateUser(User user) {
        if (user.getEmail() != null
                && userStorage.values().stream()
                .anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        userStorage.values().stream()
                .filter(s -> s.getId() == user.getId())
                .findFirst()
                .ifPresent(s -> {
                    if (user.getEmail() != null) {
                        s.setEmail(user.getEmail());
                    }

                    if (user.getName() != null) {
                        s.setName(user.getName());
                    }
                });
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.remove(id);
    }
}
