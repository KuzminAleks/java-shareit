package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto addUser(UserDto user) {
        if (!isConflict(user)) {
            return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(user)));
        }

        return user;
    }

    public UserDto updateUser(UserDto user, Integer userId) {
        if (userId == null) {
            throw new BadRequestException("Id должен быть указан.");
        }

        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        if (!isConflict(user)) {
            Optional.ofNullable(user.getName()).ifPresent(oldUser::setName);
            Optional.ofNullable(user.getEmail()).ifPresent(oldUser::setEmail);

            return UserMapper.mapToUserDto(userRepository.save(oldUser));
        }

        return user;
    }

    public UserDto getUserById(Integer userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден.")));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public boolean deleteUser(Integer userId) {
        userRepository.delete(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден.")));

        return true;
    }

    private boolean isConflict(UserDto user) {
        if (userRepository.findAll().stream()
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с email: " + user.getEmail() + " уже существует.");
        }

        return false;
    }
}
