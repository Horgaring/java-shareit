package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userStorage;

    public UserServiceImpl(UserRepository userStorage) {
        this.userStorage = userStorage;
    }


    @Override
    public User createUser(User user) {
        if (userStorage.findUserByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateException("Duplicate exception", "User with email " + user.getEmail() + " already exists");
        }

        log.info("User {} created", user);
        userStorage.save(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {

        return userStorage.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
    }


    @Override
    public User updateUser(User user) {
        if (user.getEmail() != null) {
            userStorage.findUserByEmail(user.getEmail())
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new DuplicateException("Duplicate exception", "User with email " + user.getEmail() + " already exists");
                    });
        }

        userStorage.findById(user.getId())
                .ifPresent(s -> {
                    if (user.getEmail() != null) {
                        s.setEmail(user.getEmail());
                    }

                    if (user.getName() != null) {
                        s.setName(user.getName());
                    }
                    userStorage.save(s);
                });
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteById(id);
    }
}
