package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dal.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer userId, ItemDto item);

    ItemDto updateItem(Integer itemId, Integer userId, ItemDto item);

    ItemDto getItemById(Integer itemId);

    List<ItemDto> getItemsOfOwner(Integer userId);

    List<ItemDto> getItemsByText(String text);
}
