package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
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

    @Query("select b from Booking b " +
            "where b.item.ownerId = :ownerId and b.start <= :now and b.end >= :now " +
            "order by b.start desc")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") Instant now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, Instant now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, Instant now);
}
