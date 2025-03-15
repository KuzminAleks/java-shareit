package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingStatusDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingStatusDto changeBookingStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @PathVariable Integer bookingId,
                                                  @RequestParam("approved") boolean isApproved) {
        return bookingService.changeBookingStatus(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingStatusDto getBookingStatusById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable String bookingId) {
        return bookingService.getBookingStatusById(userId, bookingId);
    }

    @GetMapping
    public List<BookingStatusDto> getBookingStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingStatus(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingStatusDto> getBookingStatusOfOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingStatusOfOwner(userId, state);
    }
}
