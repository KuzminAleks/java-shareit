package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithAnswerDto> getRequests(Integer userId);

    List<ItemRequestDto> getAllRequests();

    ItemRequestWithAnswerDto getRequestById(Integer requestId);
}
