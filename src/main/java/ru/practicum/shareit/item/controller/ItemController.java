package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dal.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl memStorage;

    @Autowired
    public ItemController(ItemServiceImpl itemStorage) {
        memStorage = itemStorage;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto item) {
        return memStorage.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody ItemDto item) {
        return memStorage.updateItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        return memStorage.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemOfOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return memStorage.getItemOfOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam("text") String text) {
        return memStorage.getItemByText(text);
    }
}
