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
import java.util.List;
import java.util.stream.Collectors;

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
    public BookingStatusDto addBooking(Integer userId, BookingDto bookingDto) {
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

            return BookingMapper.mapToBookingStatusDto(bookingRepository.save(newBooking));
        } else {
            throw new BadRequestException("Предмет с id: " + item.getId() + " не доступен для аренды.");
        }
    }

    @Override
    public BookingStatusDto changeBookingStatus(Integer userId, Integer bookingId, boolean isApproved) {
        Booking oldBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id: " + bookingId + " не найдена."));

        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Пользователя с id: "+ userId +" не существует."));

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

    @Override
    public BookingStatusDto getBookingStatusById(Integer userId, String bookingId) {
        try {
            Integer.parseInt(bookingId);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Неправильное id: " + bookingId);
        }

        Booking booking = bookingRepository.findById(Integer.parseInt(bookingId))
                .orElseThrow(() -> new NotFoundException("Аренда с id: " + bookingId + " не найдена."));

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getUserOwner().getId().equals(userId)) {
            throw new BadRequestException("У Вас нет доступа.");
        }

        return BookingMapper.mapToBookingStatusDto(booking);
    }

    @Override
    public List<BookingStatusDto> getBookingStatus(Integer userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Пользователя с id: "+ userId +" не существует."));

        List<Booking> sortedBooking = bookingRepository.getAllBookingOfBooker(userId);

        return switch (state) {
            case "CURRENT" -> sortedBooking.stream()
                    .filter(booking -> (booking.getStartTime().isBefore(LocalDateTime.now())
                            && booking.getEndTime().isAfter(LocalDateTime.now()))
                            || (booking.getStartTime().equals(LocalDateTime.now())
                            || booking.getEndTime().equals(LocalDateTime.now()))
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "PAST" -> sortedBooking.stream()
                    .filter(booking -> booking.getEndTime().isBefore(LocalDateTime.now())
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "FUTURE" -> sortedBooking.stream()
                    .filter(booking -> booking.getStartTime().isAfter(LocalDateTime.now())
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "WAITING" -> sortedBooking.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "REJECTED" -> sortedBooking.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            default -> sortedBooking.stream()
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
        };
    }

    @Override
    public List<BookingStatusDto> getBookingStatusOfOwner(Integer userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id: "+ userId +" не существует."));

        List<Booking> sortedBooking = bookingRepository.findBookingsByOwner(userId);

        return switch (state) {
            case "CURRENT" -> sortedBooking.stream()
                    .filter(booking -> (booking.getStartTime().isBefore(LocalDateTime.now())
                            && booking.getEndTime().isAfter(LocalDateTime.now()))
                            || (booking.getStartTime().equals(LocalDateTime.now())
                            || booking.getEndTime().equals(LocalDateTime.now()))
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "PAST" -> sortedBooking.stream()
                    .filter(booking -> booking.getEndTime().isBefore(LocalDateTime.now())
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "FUTURE" -> sortedBooking.stream()
                    .filter(booking -> booking.getStartTime().isAfter(LocalDateTime.now())
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "WAITING" -> sortedBooking.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            case "REJECTED" -> sortedBooking.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
            default -> sortedBooking.stream()
                    .map(BookingMapper::mapToBookingStatusDto)
                    .collect(Collectors.toList());
        };
    }
}
