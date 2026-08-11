package com.example.booking_system.controller;

import com.example.booking_system.dto.response.BookingResponse;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.security.CustomUserDetailsService;
import com.example.booking_system.security.JwtAuthenticationFilter;
import com.example.booking_system.security.JwtTokenProvider;
import com.example.booking_system.security.PermissionAuthorizationFilter;
import com.example.booking_system.security.PermissionMapping;
import com.example.booking_system.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private PermissionAuthorizationFilter permissionAuthorizationFilter;

    @MockitoBean
    private PermissionMapping permissionMapping;

    @Test
    void getAllBookings_WithAllQueryParameters_ReturnsCorrectPageData() throws Exception {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setTitle("Team Sync");
        bookingResponse.setStatus(BookingStatus.APPROVED);
        bookingResponse.setStartTime(LocalDateTime.of(2026, 8, 11, 10, 0));
        bookingResponse.setEndTime(LocalDateTime.of(2026, 8, 11, 11, 0));

        Page<BookingResponse> page = new PageImpl<>(Collections.singletonList(bookingResponse));

        when(bookingService.getAllBookings(eq("2026-08-11"), eq(BookingStatus.APPROVED), eq("Team"), any(Pageable.class), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/bookings")
                        .param("date", "2026-08-11")
                        .param("status", "APPROVED")
                        .param("q", "Team")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "startTime,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Team Sync"))
                .andExpect(jsonPath("$.content[0].status").value("APPROVED"));

        verify(bookingService).getAllBookings(eq("2026-08-11"), eq(BookingStatus.APPROVED), eq("Team"), any(Pageable.class), any());
    }
}
