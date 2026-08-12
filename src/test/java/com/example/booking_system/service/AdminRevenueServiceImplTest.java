package com.example.booking_system.service;

import com.example.booking_system.dto.response.RoomRevenueResponse;
import com.example.booking_system.model.Booking;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Room;
import com.example.booking_system.repository.BookingRepository;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.service.impl.AdminRevenueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminRevenueServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminRevenueServiceImpl adminRevenueService;

    private Room room1;
    private Room room2;

    @BeforeEach
    void setUp() {
        room1 = new Room();
        room1.setId(1L);
        room1.setName("Alpha Room");
        room1.setLockedDepartmentId(1L);
        room1.setPricePerHour(new BigDecimal("100.00"));

        room2 = new Room();
        room2.setId(2L);
        room2.setName("Beta Room");
        room2.setLockedDepartmentId(1L);
        room2.setPricePerHour(new BigDecimal("200.00"));
    }

    @Test
    void getRevenueByRoom_ReturnsPaginatedAndFilteredData() {
        LocalDateTime start1 = LocalDateTime.of(2026, 8, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2026, 8, 1, 11, 0); // 2 hours = 200.00

        Booking b1 = new Booking();
        b1.setId(10L);
        b1.setRoomId(1L);
        b1.setStatus(BookingStatus.APPROVED);
        b1.setStartTime(start1);
        b1.setEndTime(end1);
        b1.setPricePerHour(new BigDecimal("100.00"));
        b1.setAmount(new BigDecimal("200.00"));

        Booking b2 = new Booking();
        b2.setId(11L);
        b2.setRoomId(2L);
        b2.setStatus(BookingStatus.APPROVED);
        b2.setStartTime(start1);
        b2.setEndTime(end1);
        b2.setPricePerHour(new BigDecimal("200.00"));
        b2.setAmount(new BigDecimal("400.00"));

        when(bookingRepository.findByStartTimeBetween(any(), any())).thenReturn(Arrays.asList(b1, b2));
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));
        when(roomRepository.findByNameContainingIgnoreCase("Alpha")).thenReturn(Collections.singletonList(room1));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "amount"));

        Page<RoomRevenueResponse> result = adminRevenueService.getRevenueByRoom("2026-08", "Alpha", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        RoomRevenueResponse item = result.getContent().get(0);
        assertEquals(1L, item.getRoomId());
        assertEquals("Alpha Room", item.getRoomName());
        assertEquals(1, item.getBookingCount());
        assertEquals(new BigDecimal("200.00"), item.getAmount());
        // total amount of month = 600.00, share = 200 / 600 * 100 = 33.33%
        assertEquals(new BigDecimal("33.33"), item.getSharePercent());
    }

    @Test
    void exportRevenueCsv_ReturnsValidCsvData() {
        LocalDateTime start1 = LocalDateTime.of(2026, 8, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2026, 8, 1, 11, 0);

        Booking b1 = new Booking();
        b1.setId(10L);
        b1.setRoomId(1L);
        b1.setStatus(BookingStatus.APPROVED);
        b1.setStartTime(start1);
        b1.setEndTime(end1);
        b1.setPricePerHour(new BigDecimal("100.00"));
        b1.setAmount(new BigDecimal("200.00"));

        when(bookingRepository.findByStartTimeBetween(any(), any())).thenReturn(Collections.singletonList(b1));
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));

        byte[] csvBytes = adminRevenueService.exportRevenueCsv("2026-08");

        assertNotNull(csvBytes);
        String csvContent = new String(csvBytes, java.nio.charset.StandardCharsets.UTF_8);

        assertTrue(csvContent.contains("Year/Month,Total Amount,Total Bookings,Average Amount,Cancelled Count,Cancellation Rate (%)"));
        assertTrue(csvContent.contains("2026-08"));
        assertTrue(csvContent.contains("Breakdown By Room"));
        assertTrue(csvContent.contains("Alpha Room"));
        assertTrue(csvContent.contains("Breakdown By Day"));
    }
}

