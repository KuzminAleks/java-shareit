package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?2 " +
            "AND b.booker.id = ?1 " +
            "AND b.endTime < ?3")
    List<Booking> findBookerAndItem(Integer bookerId, Integer itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN item i " +
            "WHERE i.userOwner.id = ?1 " +
            "AND (b.startTime <= ?2 AND b.endTime >= ?2) " +
            "AND b.status = ?3 " +
            "ORDER BY b.startTime ASC")
    List<Booking> findCurrentBookings(Integer bookerId, LocalDateTime now, Integer statusId);

    @Query("SELECT b FROM Booking b " +
            "JOIN item i " +
            "WHERE i.userOwner.id = ?1 " +
            "AND b.endTime <= ?2 " +
            "AND b.status = ?3 " +
            "ORDER BY b.startTime ASC")
    List<Booking> findPastBookings(Integer bookerId, LocalDateTime now, Integer statusId);

    @Query("SELECT b FROM Booking b " +
            "JOIN item i " +
            "WHERE i.userOwner.id = ?1 " +
            "AND b.startTime > ?2 " +
            "AND b.status = ?3 " +
            "ORDER BY b.startTime ASC")
    List<Booking> findFutureBookings(Integer bookerId, LocalDateTime now, Integer statusId);

    @Query("SELECT b FROM Booking b " +
            "JOIN item i " +
            "WHERE i.userOwner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.startTime ASC")
    List<Booking> findWaitingBookings(Integer bookerId, Integer statusId);

    @Query("SELECT b FROM Booking b " +
            "JOIN item i " +
            "WHERE i.userOwner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.startTime ASC")
    List<Booking> findRejectedBookings(Integer bookerId, Integer statusId);

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
