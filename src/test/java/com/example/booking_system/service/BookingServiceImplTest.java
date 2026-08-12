package com.example.booking_system.service;

import com.example.booking_system.dto.request.CreateBookingRequest;
import com.example.booking_system.dto.response.BookingResponse;
import com.example.booking_system.model.Booking;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Notification;
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
        room.setLockedDepartmentId(1L);
        room.setPricePerHour(new BigDecimal("100.00"));
    }

    @Test
    void createBooking_SetsPricePerHourAndAmountCorrectly() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1).plusMinutes(30); // 1.5 hours

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

    @Test
    void createBooking_ThrowsException_WhenStartTimeIsInPast() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = start.plusHours(2);

        CreateBookingRequest request = new CreateBookingRequest(1L, "Past Sync", start, end);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.createBooking(request, "user@example.com")
        );

        assertEquals("Cannot create a booking in the past", exception.getMessage());
    }

    @Test
    void getBookings_AdminUser_PassesAllFiltersToFindByDateRange() {
        com.example.booking_system.security.UserPrincipal adminPrincipal = mock(com.example.booking_system.security.UserPrincipal.class);
        when(adminPrincipal.getAuthorities()).thenAnswer(invocation -> 
            Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                com.example.booking_system.security.PermissionCodes.BOOKING_VIEW_ALL))
        );

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTitle("Sync Meeting");
        booking.setStatus(BookingStatus.APPROVED);
        booking.setRoomId(1L);
        booking.setUserId(10L);

        org.springframework.data.domain.Page<Booking> page = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(booking));
        LocalDateTime expectedStart = LocalDateTime.of(2026, 8, 11, 0, 0);
        LocalDateTime expectedEnd = LocalDateTime.of(2026, 8, 12, 0, 0);

        when(bookingRepository.findByDateRange(eq(expectedStart), eq(expectedEnd), eq(BookingStatus.APPROVED), eq("Sync"), eq(pageable)))
                .thenReturn(page);

        org.springframework.data.domain.Page<BookingResponse> result = bookingService.getBookings("2026-08-11", BookingStatus.APPROVED, "Sync", pageable, adminPrincipal);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findByDateRange(expectedStart, expectedEnd, BookingStatus.APPROVED, "Sync", pageable);
    }

    @Test
    void getBookings_NormalUser_PassesAllFiltersToFindByUserIdAndDateRange() {
        com.example.booking_system.security.UserPrincipal userPrincipal = mock(com.example.booking_system.security.UserPrincipal.class);
        when(userPrincipal.getAuthorities()).thenReturn(Collections.emptyList());
        when(userPrincipal.getId()).thenReturn(10L);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setTitle("User Meeting");
        booking.setStatus(BookingStatus.PENDING);
        booking.setRoomId(1L);
        booking.setUserId(10L);

        org.springframework.data.domain.Page<Booking> page = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(booking));

        when(bookingRepository.findByUserIdAndDateRange(eq(10L), isNull(), isNull(), eq(BookingStatus.PENDING), isNull(), eq(pageable)))
                .thenReturn(page);

        org.springframework.data.domain.Page<BookingResponse> result = bookingService.getBookings(null, BookingStatus.PENDING, "   ", pageable, userPrincipal);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findByUserIdAndDateRange(10L, null, null, BookingStatus.PENDING, null, pageable);
    }

    @Test
    void expireOverduePendingBookings_UpdatesStatusAndCreatesNotification() {
        Booking overdueBooking = new Booking();
        overdueBooking.setId(100L);
        overdueBooking.setTitle("Overdue Sync");
        overdueBooking.setStatus(BookingStatus.PENDING);
        overdueBooking.setUserId(10L);
        overdueBooking.setStartTime(LocalDateTime.now().minusMinutes(30));

        when(bookingRepository.findByStatusAndStartTimeLessThanEqual(eq(BookingStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(overdueBooking));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        int expiredCount = bookingService.expireOverduePendingBookings();

        assertEquals(1, expiredCount);
        assertEquals(BookingStatus.EXPIRED, overdueBooking.getStatus());
        verify(notificationRepository).save(any(Notification.class));
        verify(bookingRepository).saveAll(anyList());
    }
}
