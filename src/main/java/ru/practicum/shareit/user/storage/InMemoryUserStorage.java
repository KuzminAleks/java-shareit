package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();

    public Collection<User> getAllUsers() {
        return userMap.values();
    }

    public User getUserById(Integer userId) {
        Optional<User> user = Optional.ofNullable(userMap.get(userId));

        return user.orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public User addUser(User user) {
        user.setId(getNextId());

        userMap.put(user.getId(), user);

        return user;
    }

    public User updateUser(User user) {
        userMap.put(user.getId(), user);

        return user;
    }

    public boolean deleteUser(Integer userId) {
        return userMap.remove(userId) != null;
    }

    private int getNextId() {
        int currentMaxId = userMap.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
