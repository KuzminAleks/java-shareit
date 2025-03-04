package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dal.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final InMemoryUserStorage memStorage;

    @Autowired
    public UserController(InMemoryUserStorage userStorage) {
        memStorage = userStorage;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return memStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Integer userId) {
        return memStorage.getUserById(userId);
    }

    @PostMapping
    public UserDto addUser(@RequestBody User user) {
        return memStorage.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody User user, @PathVariable Integer userId) {
        return memStorage.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public boolean deleteUser(@PathVariable Integer userId) {
        return memStorage.deleteUser(userId);
    }
}
