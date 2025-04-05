package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    UserService userService;
    ItemRequestRepository requestRepository;
    ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(UserService userService, ItemRequestRepository requestRepository, ItemRepository itemRepository) {
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
    }

    public ItemRequestDto addRequest(Integer userId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setCreateTime(LocalDateTime.now());

        User user = UserMapper.mapToUser(userService.getUserById(userId));

        itemRequestDto.setUser(user);

        return ItemRequestMapper.mapToReqDto(requestRepository.save(ItemRequestMapper.mapToReq(itemRequestDto)));
    }

    public List<ItemRequestWithAnswerDto> getRequests(Integer userId) {
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreateTimeDesc(userId);

        return requests.stream()
                .map(request -> new ItemRequestWithAnswerDto(
                        request.getId(),
                        request.getDescription(),
                        request.getCreateTime(),
                        itemRepository.findByRequestId(request.getId()).stream()
                                .toList()
                ))
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests() {
        return requestRepository.findOrderByCreateTimeDesc().stream()
                .map(ItemRequestMapper::mapToReqDto)
                .collect(Collectors.toList());
    }

    public ItemRequestWithAnswerDto getRequestById(Integer requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на предмет не найден."));

        return new ItemRequestWithAnswerDto(request.getId(), request.getDescription(), request.getCreateTime(),
                itemRepository.findByRequestId(requestId).stream()
                        .toList());
    }
}
