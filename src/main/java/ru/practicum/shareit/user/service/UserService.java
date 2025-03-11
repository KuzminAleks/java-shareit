package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(UserDto user) {
        if (!isConflict(user)) {
            return UserMapper.mapToUserDto(userStorage.addUser(UserMapper.mapToUser(user)));
        }

        return user;
    }

    public UserDto updateUser(UserDto user, Integer userId) {
        if (userId == null) {
            throw new BadRequestException("Id должен быть указан.");
        }

        User oldUser = userStorage.getUserById(userId);

        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
        }

        if (!isConflict(user)) {
            Optional.ofNullable(user.getName()).ifPresent(oldUser::setName);
            Optional.ofNullable(user.getEmail()).ifPresent(oldUser::setEmail);

            return UserMapper.mapToUserDto(userStorage.updateUser(oldUser));
        }

        return user;
    }

    public UserDto getUserById(Integer userId) {
        return UserMapper.mapToUserDto(userStorage.getUserById(userId));
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public boolean deleteUser(Integer userId) {
        return userStorage.deleteUser(userId);
    }

    private boolean isConflict(UserDto user) {
        if (userStorage.getAllUsers().stream()
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с email: " + user.getEmail() + " уже существует.");
        }

        return false;
    }
}
