package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
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

    public UserDto getUserById(Integer userId) {
        Optional<User> user = Optional.ofNullable(userMap.get(userId));

        return user.map(UserMapper::mapToUserDto).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public UserDto addUser(User user) {
        if (isValidated(user, false)) {
            user.setId(getNextId());

            userMap.put(user.getId(), user);
        }

        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(User newUser, Integer userId) {
        if (isValidated(newUser, true)) {
            if (userId == null) {
                throw new BadRequestException("Id должен быть указан.");
            }

            if (userMap.containsKey(userId)) {
                User oldUser = userMap.get(userId);

                if (newUser.getName() != null) {
                    oldUser.setName(newUser.getName());
                }

                if (newUser.getEmail() != null) {
                    oldUser.setEmail(newUser.getEmail());
                }

                userMap.put(userId, oldUser);

                return UserMapper.mapToUserDto(oldUser);
            }
        }

        throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
    }

    public boolean deleteUser(Integer userId) {
        return userMap.remove(userId) != null;
    }

    private boolean isValidated(User user, boolean isUpdate) {
        if (isUpdate) {
            if (user.getEmail() == null) {
                if (user.getName() == null) {
                    throw new BadRequestException("Параметры для изменения должны быть указаны.");
                }
            } else {
                if (user.getEmail().isBlank()
                        || !user.getEmail().contains("@")) {
                    throw new BadRequestException("Неверная почта.");
                }
            }
        } else {
            if (user.getEmail() == null
                    || user.getEmail().isBlank()
                    || !user.getEmail().contains("@")) {
                throw new BadRequestException("Неверная почта.");
            }

            if (user.getName() == null) {
                throw new BadRequestException("Имя должно быть указано.");
            }
        }

        if (!userMap.isEmpty()) {
            if (userMap.values().stream()
                    .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))
            ) {
                throw new ConflictException("Пользователь с email: " + user.getEmail() + " уже существует.");
            }
        }

        return true;
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
