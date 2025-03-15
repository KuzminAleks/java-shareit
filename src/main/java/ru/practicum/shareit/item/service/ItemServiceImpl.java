package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(Integer userId, ItemDto item) {
        Item newItem = ItemMapper.mapToItem(item);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        newItem.setUserOwner(user);

        return ItemMapper.mapToItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto item) {
        Item oldItem = itemRepository.findById(itemId)
                .filter(i -> i.getUserOwner().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден."));

        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);
        Optional.ofNullable(item.getRequest()).ifPresent(oldItem::setRequest);

        return ItemMapper.mapToItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.mapToItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден.")));
    }

    @Override
    public List<ItemDto> getItemsOfOwner(Integer userId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getUserOwner().getId().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findAll().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
