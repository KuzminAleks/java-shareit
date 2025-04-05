package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemStorage) {
        itemService = itemStorage;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto item,
                           @RequestBody(required = false) Integer requestId) {
        return itemService.addItem(userId, item, requestId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemOfOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemsOfOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam("text") String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentCreatedDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId,
                                        @RequestBody CommentCreatedDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }
}
