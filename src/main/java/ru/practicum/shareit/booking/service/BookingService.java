package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;

import java.util.List;

public interface BookingService {
    BookingStatusDto addBooking(Integer userId, BookingDto bookingDto);

    BookingStatusDto changeBookingStatus(Integer userId, Integer bookingId, boolean isApproved);

    BookingStatusDto getBookingStatusById(Integer userId, String bookingId);

    List<BookingStatusDto> getBookingStatus(Integer userId, String state);

    List<BookingStatusDto> getBookingStatusOfOwner(Integer userId, String state);
}
