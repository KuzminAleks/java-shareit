package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.dao.ItemDbStorage;
import ru.practicum.shareit.item.dal.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
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
        Item newItem = ItemMapper.mapToItem(item);

        User user = userStorage.getUserById(userId);

        if (user != null) {
            newItem.setUserOwner(user);
        } else {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
        }

        return ItemMapper.mapToItemDto(itemDbStorage.addItem(newItem));
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto item) {
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

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.mapToItemDto(itemDbStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsOfOwner(Integer userId) {
        return itemDbStorage.getItemOfOwner(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        } else {
            text = text.toLowerCase();
        }

        return itemDbStorage.getItemByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
