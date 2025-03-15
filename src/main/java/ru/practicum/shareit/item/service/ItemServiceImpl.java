package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto addItem(Integer userId, ItemDto item) {
        Item newItem = ItemMapper.mapToItem(item);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        newItem.setUserOwner(user);

        return ItemMapper.mapToItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto item) {
        Item oldItem = itemRepository.findById(itemId)
                .filter(i -> i.getUserOwner().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден."));

        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);
        Optional.ofNullable(item.getRequest()).ifPresent(oldItem::setRequest);

        return ItemMapper.mapToItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден."));

        List<CommentDto> comments = commentRepository.findByItem(item).stream()
                .map(CommentMapper::mapToItemWithComments)
                .toList();

        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getItem().equals(item))
                .toList();

        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        itemDto.setComments(comments);
        itemDto.setNextBooking(null);
        itemDto.setLastBooking(null);

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsOfOwner(Integer userId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getUserOwner().getId().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findAll().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentCreatedDto addComment(Integer userId, Integer itemId, CommentCreatedDto comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден."));

        List<Booking> userBooking = bookingRepository.findByBookerAndItem(user, item);

        if (!userBooking.isEmpty()) {
            if (userBooking.getFirst().getEndTime().isBefore(LocalDateTime.now())) {
                Comment newComment = new Comment();

                newComment.setItem(item);
                newComment.setAuthor(user);
                newComment.setText(comment.getText());
                newComment.setCreated(LocalDateTime.now());

                return CommentMapper.mapToCommentCreatedDto(commentRepository.save(newComment));
            }
        }

        throw new BadRequestException("Пользователь не пользовался вещью.");
    }
}
