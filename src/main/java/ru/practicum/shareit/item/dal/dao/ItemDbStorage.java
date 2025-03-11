package ru.practicum.shareit.item.dal.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemDbStorage {
    Map<Integer, Item> itemMap = new HashMap<>();

    public Item addItem(Item item) {
        item.setId(getNextId());

        itemMap.put(item.getId(), item);

        return item;
    }

    public Item updateItem(Item item) {
        itemMap.put(item.getId(), item);

        return item;
    }

    public Item getItemById(Integer itemId) {
        return itemMap.get(itemId);
    }

    public List<Item> getItemOfOwner(Integer userId) {
        return itemMap.values().stream()
                .filter(item -> item.getUserOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> getItemByText(String text) {
        return itemMap.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
    }

    private int getNextId() {
        int currentMaxId = itemMap.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
