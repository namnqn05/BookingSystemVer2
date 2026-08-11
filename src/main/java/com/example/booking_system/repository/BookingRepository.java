package com.example.booking_system.repository;

import com.example.booking_system.model.Booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
           "WHERE b.startTime < :end AND b.endTime > :start ")
    Page<Booking> findByDateRange(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  Pageable pageable);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.userId = :userId AND b.startTime < :end AND b.endTime > :start ")
    Page<Booking> findByUserIdAndDateRange(@Param("userId") Long userId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           Pageable pageable);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.roomId = :roomId " +
           "AND b.status IN (com.example.booking_system.model.BookingStatus.PENDING, " +
           "                 com.example.booking_system.model.BookingStatus.APPROVED) " +
           "AND :startTime < b.endTime AND b.startTime < :endTime")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND (:q IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Booking> findBookingsByUserIdWithSearch(@Param("userId") Long userId, @Param("q") String q, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.startTime >= :start AND b.startTime < :end")
    List<Booking> findByStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Booking> findByUserIdOrderByStartTimeDesc(Long userId);
}
