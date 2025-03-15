package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto) {
        return new Booking(null, bookingDto.getStartTime(), bookingDto.getEndTime(),
                null, null, null);
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getItem().getId(), booking.getStartTime(), booking.getEndTime());
    }

    public static BookingStatusDto mapToBookingStatusDto(Booking booking) {
        return new BookingStatusDto(booking.getId(), booking.getStartTime(), booking.getEndTime(), booking.getStatus(),
                booking.getBooker().getId(), booking.getItem().getId(), booking.getItem().getName());
    }
}
