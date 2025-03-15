package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto addBooking(Integer userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        if (bookingDto.getStartTime().isAfter(bookingDto.getEndTime())) {
            throw new BadRequestException("Дата начала аренды не может быть после даты конца аренды.");
        }

        if (bookingDto.getStartTime().isEqual(bookingDto.getEndTime())) {
            throw new BadRequestException("Дата начала аренды не может быть равна дате конца аренды.");
        }

        if (bookingDto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала аренды не может быть в прошлом относительно нынешней даты.");
        }

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + bookingDto.getItemId() + " не найден."));

        if (item.isAvailable()) {
            Booking newBooking = new Booking();

            newBooking.setStartTime(bookingDto.getStartTime());
            newBooking.setEndTime(bookingDto.getEndTime());
            newBooking.setItem(item);
            newBooking.setBooker(user);
            newBooking.setStatus(BookingStatus.WAITING);

            return BookingMapper.mapToBookingDto(bookingRepository.save(newBooking));
        } else {
            throw new BadRequestException("Предмет с id: " + item.getId() + " не доступен для аренды.");
        }
    }

    public BookingStatusDto changeStatusOfBooking(Integer userId, Integer bookingId, boolean isApproved) {
        Booking oldBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id: " + bookingId + " не найдена."));

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        Item item = oldBooking.getItem();

        if (item.getUserOwner().getId().equals(userId)) {
            if (isApproved) {
                oldBooking.setStatus(BookingStatus.APPROVED);
            } else {
                oldBooking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new BadRequestException("Вы не являетесь владельцем этой вещи.");
        }

        return BookingMapper.mapToBookingStatusDto(bookingRepository.save(oldBooking));
    }
}
