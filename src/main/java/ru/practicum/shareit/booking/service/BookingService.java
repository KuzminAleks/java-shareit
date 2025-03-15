package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;

public interface BookingService {
    BookingDto addBooking(Integer userId, BookingDto bookingDto);

    BookingStatusDto changeStatusOfBooking(Integer userId, Integer bookingId, boolean isApproved);
}
