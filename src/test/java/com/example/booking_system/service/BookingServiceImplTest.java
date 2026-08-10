package com.example.booking_system.service;

import com.example.booking_system.dto.request.CreateBookingRequest;
import com.example.booking_system.dto.response.BookingResponse;
import com.example.booking_system.model.Booking;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Role;
import com.example.booking_system.model.Room;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.BookingRepository;
import com.example.booking_system.repository.NotificationRepository;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Room room;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");
        user.setFullName("User Test");
        user.setRole(Role.ROLE_USER);

        room = new Room();
        room.setId(1L);
        room.setName("Conference Room A");
        room.setCapacity(10);
        room.setIsActive(true);
        room.setPricePerHour(new BigDecimal("100.00"));
    }

    @Test
    void createBooking_SetsPricePerHourAndAmountCorrectly() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 10, 11, 30); // 1.5 hours

        CreateBookingRequest request = new CreateBookingRequest(1L, "Team Sync", start, end);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bookingRepository.existsOverlappingBooking(1L, start, end)).thenReturn(false);
        when(userRepository.findByRole(Role.ROLE_ADMIN)).thenReturn(Collections.emptyList());

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(100L);
            return b;
        });

        BookingResponse response = bookingService.createBooking(request, "user@example.com");

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getPricePerHour());
        assertEquals(new BigDecimal("150.00"), response.getAmount());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();
        assertEquals(new BigDecimal("100.00"), savedBooking.getPricePerHour());
        assertEquals(new BigDecimal("150.00"), savedBooking.getAmount());
        assertEquals(BookingStatus.PENDING, savedBooking.getStatus());
    }
}
