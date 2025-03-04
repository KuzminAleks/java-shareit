package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dal.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(), item.getRequest());
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(itemDto.getRequest());

        return item;
    }
}
