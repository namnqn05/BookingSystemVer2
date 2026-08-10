package com.example.booking_system.service;

import com.example.booking_system.dto.request.CreateRoomRequest;
import com.example.booking_system.dto.request.UpdateRoomRequest;
import com.example.booking_system.dto.response.RoomResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    Page<RoomResponse> getAllRooms(Pageable pageable);
    RoomResponse createRoom(CreateRoomRequest request);
    RoomResponse updateRoom(Long id, UpdateRoomRequest request);
}
