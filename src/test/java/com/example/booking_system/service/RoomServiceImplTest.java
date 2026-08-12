package com.example.booking_system.service;

import com.example.booking_system.dto.request.CreateRoomRequest;
import com.example.booking_system.dto.request.UpdateRoomRequest;
import com.example.booking_system.dto.response.RoomResponse;
import com.example.booking_system.model.Room;
import com.example.booking_system.repository.DepartmentRepository;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private CreateRoomRequest createRequest;
    private UpdateRoomRequest updateRequest;
    private Room existingRoom;

    @BeforeEach
    void setUp() {
        createRequest = new CreateRoomRequest("Test Room", 10, true, new BigDecimal("50.00"), 1L);
        updateRequest = new UpdateRoomRequest("Updated Room", 15, true, new BigDecimal("60.00"), 2L);

        existingRoom = new Room();
        existingRoom.setId(100L);
        existingRoom.setName("Test Room");
        existingRoom.setCapacity(10);
        existingRoom.setIsActive(true);
        existingRoom.setLockedDepartmentId(1L);
        existingRoom.setPricePerHour(new BigDecimal("50.00"));
    }

    @Test
    void getAllRooms_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> page = new PageImpl<>(List.of(existingRoom));
        when(roomRepository.findAllRoomsWithPaginationAndSearch(null, null, pageable)).thenReturn(page);

        Page<RoomResponse> result = roomService.getAllRooms(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Room", result.getContent().get(0).getName());
        verify(roomRepository, times(1)).findAllRoomsWithPaginationAndSearch(null, null, pageable);
    }

    @Test
    void getAllRooms_WithSearchAndActiveFilter_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> page = new PageImpl<>(List.of(existingRoom));
        when(roomRepository.findAllRoomsWithPaginationAndSearch("Test", true, pageable)).thenReturn(page);

        Page<RoomResponse> result = roomService.getAllRooms("Test", true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Room", result.getContent().get(0).getName());
        verify(roomRepository, times(1)).findAllRoomsWithPaginationAndSearch("Test", true, pageable);
    }

    @Test
    void createRoom_Success() {
        when(roomRepository.existsByName("Test Room")).thenReturn(false);
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room r = invocation.getArgument(0);
            r.setId(100L);
            return r;
        });

        RoomResponse response = roomService.createRoom(createRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Test Room", response.getName());
        assertEquals(1L, response.getLockedDepartmentId());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void createRoom_MissingLockedDepartmentId_ThrowsException() {
        createRequest.setLockedDepartmentId(null);
        when(roomRepository.existsByName("Test Room")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.createRoom(createRequest);
        });

        assertEquals("Locked department ID is required", exception.getMessage());
        verify(roomRepository, never()).save(any());
    }

    @Test
    void createRoom_DepartmentNotFound_ThrowsException() {
        when(roomRepository.existsByName("Test Room")).thenReturn(false);
        when(departmentRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.createRoom(createRequest);
        });

        assertEquals("Department not found with ID: 1", exception.getMessage());
        verify(roomRepository, never()).save(any());
    }

    @Test
    void updateRoom_Success() {
        when(roomRepository.findById(100L)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.existsByName("Updated Room")).thenReturn(false);
        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoomResponse response = roomService.updateRoom(100L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Room", response.getName());
        assertEquals(2L, response.getLockedDepartmentId());
        verify(roomRepository, times(1)).save(existingRoom);
    }

    @Test
    void updateRoom_DepartmentNotFound_ThrowsException() {
        when(roomRepository.findById(100L)).thenReturn(Optional.of(existingRoom));
        when(departmentRepository.existsById(2L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.updateRoom(100L, updateRequest);
        });

        assertEquals("Department not found with ID: 2", exception.getMessage());
        verify(roomRepository, never()).save(any());
    }
}
