package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ItemRequestWithAnswerDto getRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return null;
    }
}
