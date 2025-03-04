package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.dao.ItemDbStorage;
import ru.practicum.shareit.item.dal.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    InMemoryUserStorage userStorage;
    ItemDbStorage itemDbStorage;

    @Autowired
    public ItemServiceImpl(ItemDbStorage itemDbStorage, InMemoryUserStorage userStorage) {
        this.itemDbStorage = itemDbStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto addItem(Integer userId, ItemDto item) {
        if (isAvailable(item, false)) {
            Item newItem = ItemMapper.mapToItem(item);

            UserDto user = userStorage.getUserById(userId);

            if (user != null) {
                newItem.setUserOwner(UserMapper.mapToUser(user));
            } else {
                throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
            }

            newItem.setId(getNextId());


            return ItemMapper.mapToItemDto(itemDbStorage.addItem(newItem));
        }

        throw new InternalServerException("Пользователь не добавлен.");
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto item) {
        if (isAvailable(item, true)) {
            if (itemDbStorage.getItemById(itemId).getUserOwner().getId().equals(userId)) {
                Item oldItem = itemDbStorage.getItemById(itemId);

                Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
                Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
                Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);
                Optional.ofNullable(item.getRequest()).ifPresent(oldItem::setRequest);

                return ItemMapper.mapToItemDto(itemDbStorage.updateItem(oldItem));
            } else {
                throw new NotFoundException("Предмет с id: " + itemId + " не найден.");
            }
        }

        throw new InternalServerException("Пользователь не обновлен.");
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.mapToItemDto(itemDbStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemOfOwner(Integer userId) {
        return itemDbStorage.getItemOfOwner(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        return itemDbStorage.getItemByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    private boolean isAvailable(ItemDto item, boolean isUpdate) {
        if (isUpdate) {
            if (item.getName() != null) {
                if (item.getName().isBlank()) {
                    throw new BadRequestException("Предмет должен иметь название.");
                }

                return true;
            }

            if (item.getDescription() != null) {
                if (item.getDescription().isBlank()) {
                    throw new BadRequestException("Предмет должен иметь описание.");
                }

                return true;
            }

            if (item.getAvailable() == null) {
                throw new BadRequestException("Поле available не указано.");
            }
        } else {

            if (item.getName() == null || item.getName().isBlank()) {
                throw new BadRequestException("Предмет должен иметь название.");
            }

            if (item.getDescription() == null || item.getDescription().isBlank()) {
                throw new BadRequestException("Предмет должен иметь описание.");
            }

            if (item.getAvailable() == null) {
                throw new BadRequestException("Поле available не указано.");
            }
        }
        return true;
    }

    private int getNextId() {
        int currentMaxId = itemDbStorage.getAllItems().keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
