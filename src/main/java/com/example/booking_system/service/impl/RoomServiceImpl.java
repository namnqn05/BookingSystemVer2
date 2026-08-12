package com.example.booking_system.service.impl;

import com.example.booking_system.dto.request.CreateRoomRequest;
import com.example.booking_system.dto.request.UpdateRoomRequest;
import com.example.booking_system.dto.response.RoomResponse;
import com.example.booking_system.model.Room;
import com.example.booking_system.repository.DepartmentRepository;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.service.RoomService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final DepartmentRepository departmentRepository;

    public RoomServiceImpl(RoomRepository roomRepository, DepartmentRepository departmentRepository) {
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return getAllRooms(null, null, pageable);
    }

    @Override
    public Page<RoomResponse> getAllRooms(String q, Boolean active, Pageable pageable) {
        String searchQuery = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        return roomRepository.findAllRoomsWithPaginationAndSearch(searchQuery, active, pageable)
                .map(RoomResponse::fromEntity);
    }

    @Override
    public RoomResponse createRoom(CreateRoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Room with name '" + request.getName() + "' already exists");
        }
        if (request.getLockedDepartmentId() == null) {
            throw new IllegalArgumentException("Locked department ID is required");
        }
        if (!departmentRepository.existsById(request.getLockedDepartmentId())) {
            throw new IllegalArgumentException("Department not found with ID: " + request.getLockedDepartmentId());
        }

        Room room = new Room();
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        room.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        room.setPricePerHour(request.getPricePerHour() != null ? request.getPricePerHour() : BigDecimal.ZERO);
        room.setLockedDepartmentId(request.getLockedDepartmentId());

        Room savedRoom = roomRepository.save(room);
        return RoomResponse.fromEntity(savedRoom);
    }

    @Override
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            if (!room.getName().equals(request.getName()) && roomRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Room with name '" + request.getName() + "' already exists");
            }
            room.setName(request.getName());
        }

        if (request.getCapacity() != null) {
            room.setCapacity(request.getCapacity());
        }

        if (request.getIsActive() != null) {
            room.setIsActive(request.getIsActive());
        }

        if (request.getPricePerHour() != null) {
            room.setPricePerHour(request.getPricePerHour());
        }

        if (request.getLockedDepartmentId() != null) {
            if (!departmentRepository.existsById(request.getLockedDepartmentId())) {
                throw new IllegalArgumentException("Department not found with ID: " + request.getLockedDepartmentId());
            }
            room.setLockedDepartmentId(request.getLockedDepartmentId());
        }

        Room updatedRoom = roomRepository.save(room);
        return RoomResponse.fromEntity(updatedRoom);
    }
}
