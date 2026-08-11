package com.example.booking_system.service;

import com.example.booking_system.dto.request.CreateBookingRequest;
import com.example.booking_system.dto.response.BookingResponse;
import com.example.booking_system.security.UserPrincipal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    Page<BookingResponse> getBookings(String date, Pageable pageable, UserPrincipal principal);

    BookingResponse createBooking(CreateBookingRequest request, String userEmail);

    BookingResponse approveBooking(Long id, String username);

    BookingResponse rejectBooking(Long id, String username);

    BookingResponse cancelBooking(Long id, String userEmail);
}
