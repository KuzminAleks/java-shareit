package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer userId, ItemDto item);

    ItemDto updateItem(Integer itemId, Integer userId, ItemDto item);

    ItemDto getItemById(Integer itemId);

    List<ItemDto> getItemsOfOwner(Integer userId);

    List<ItemDto> getItemsByText(String text);

    CommentCreatedDto addComment(Integer userId, Integer itemId, CommentCreatedDto comment);
}
