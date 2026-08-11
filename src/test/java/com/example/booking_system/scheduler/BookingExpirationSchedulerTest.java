package com.example.booking_system.scheduler;

import com.example.booking_system.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingExpirationSchedulerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingExpirationScheduler scheduler;

    @Test
    void expireOverdueBookings_CallsServiceMethod() {
        when(bookingService.expireOverduePendingBookings()).thenReturn(2);

        scheduler.expireOverdueBookings();

        verify(bookingService, times(1)).expireOverduePendingBookings();
    }
}
