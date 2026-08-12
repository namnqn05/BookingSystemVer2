package com.example.booking_system.repository;

import com.example.booking_system.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String name);
    List<Room> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r FROM Room r WHERE " +
           "(CAST(:q AS string) IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))) " +
           "AND (CAST(:active AS boolean) IS NULL OR r.isActive = :active)")
    Page<Room> findAllRoomsWithPaginationAndSearch(@Param("q") String q, @Param("active") Boolean active, Pageable pageable);
}
