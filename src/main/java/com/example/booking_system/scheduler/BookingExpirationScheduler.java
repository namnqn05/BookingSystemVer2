package com.example.booking_system.scheduler;

import com.example.booking_system.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingExpirationScheduler.class);
    private final BookingService bookingService;

    public BookingExpirationScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void expireOverdueBookings() {
        try {
            int expiredCount = bookingService.expireOverduePendingBookings();
            if (expiredCount > 0) {
                log.info("Successfully expired {} overdue PENDING booking(s).", expiredCount);
            }
        } catch (Exception e) {
            log.error("Error occurred while expiring overdue bookings", e);
        }
    }
}
