package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerAndItem(User booker, Item item);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE booker_id = ?1 " +
            "ORDER BY start_date ASC;", nativeQuery = true)
    List<Booking> getAllBookingOfBooker(Integer bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.userOwner.id = ?1" +
            "ORDER BY b.startTime ASC")
    List<Booking> findBookingsByOwner(Integer userId);
}
