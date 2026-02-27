package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_IdOrderByStartDesc(Long userId);

    List<Booking> findByUser_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("select b from Booking b " +
            "where b.user.id = :userId and b.start <= :now and b.end >= :now " +
            "order by b.start desc")
    List<Booking> findCurrentByUserId(@Param("userId") Long userId, @Param("now") Instant now);

    List<Booking> findByUser_IdAndEndBeforeOrderByStartDesc(Long userId, Instant now);

    List<Booking> findByUser_IdAndStartAfterOrderByStartDesc(Long userId, Instant now);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.user.id = :bookerId")
    List<Booking> findByItemIdAndBookerId2(@Param("itemId") Long itemId, @Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.end < :now AND b.status = 'APPROVED' ORDER BY b.end DESC")
    List<Booking> findLastBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :now AND b.status = 'APPROVED' ORDER BY b.start ASC")
    List<Booking> findNextBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.ownerId = :ownerId and b.start <= :now and b.end >= :now " +
            "order by b.start desc")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") Instant now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, Instant now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, Instant now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.user.id = :bookerId AND b.status = :status AND b.start <= :now")
    List<Booking> findByItemIdAndBookerId(@Param("itemId") Long itemId, @Param("bookerId") Long bookerId, @Param("status") BookingStatus status, @Param("now") LocalDateTime now);
}
