package com.example.booking_system.repository;

import com.example.booking_system.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String name);
    List<Room> findByNameContainingIgnoreCase(String name);
}
