package com.example.booking_system.service;

import com.example.booking_system.dto.response.RoomRevenueResponse;
import com.example.booking_system.dto.response.RevenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminRevenueService {
    RevenueResponse getRevenue(String yearMonth);
    Page<RoomRevenueResponse> getRevenueByRoom(String yearMonth, String q, Pageable pageable);
    byte[] exportRevenueCsv(String yearMonth);
}
