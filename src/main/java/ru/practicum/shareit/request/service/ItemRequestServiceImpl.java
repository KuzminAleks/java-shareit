package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    UserService userService;
    ItemRequestRepository requestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserService userService, ItemRequestRepository requestRepository) {
        this.userService = userService;
        this.requestRepository = requestRepository;
    }

    public ItemRequestDto addRequest(Integer userId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setCreateTime(LocalDateTime.now());

        return ItemRequestMapper.mapToReqDto(requestRepository.save(ItemRequestMapper.mapToReq(itemRequestDto)));
    }

    public List<ItemRequestWithAnswerDto> getRequests(Integer userId) {
        List<ItemRequest> itemRequests = requestRepository.findRequestsWithItems(userId);

        return null;
    }
}
