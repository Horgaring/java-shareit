package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserClient userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public  ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDto user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> etUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
