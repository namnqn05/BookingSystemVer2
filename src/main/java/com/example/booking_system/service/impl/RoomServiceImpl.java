package com.example.booking_system.service.impl;

import com.example.booking_system.dto.request.CreateRoomRequest;
import com.example.booking_system.dto.request.UpdateRoomRequest;
import com.example.booking_system.dto.response.RoomResponse;
import com.example.booking_system.model.Room;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.service.RoomService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable).map(RoomResponse::fromEntity);
    }

    @Override
    public RoomResponse createRoom(CreateRoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Room with name '" + request.getName() + "' already exists");
        }

        Room room = new Room();
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        room.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

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

        Room updatedRoom = roomRepository.save(room);
        return RoomResponse.fromEntity(updatedRoom);
    }
}
