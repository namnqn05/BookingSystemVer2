package com.example.booking_system.controller;

import com.example.booking_system.dto.response.RevenueResponse;
import com.example.booking_system.dto.response.RoomRevenueResponse;
import com.example.booking_system.service.AdminRevenueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/admin/revenue", "/admin/revenue"})
public class AdminRevenueController {

    private final AdminRevenueService adminRevenueService;

    public AdminRevenueController(AdminRevenueService adminRevenueService) {
        this.adminRevenueService = adminRevenueService;
    }

    @GetMapping
    public ResponseEntity<RevenueResponse> getRevenue(@RequestParam(required = false) String yearMonth) {
        return ResponseEntity.ok(adminRevenueService.getRevenue(yearMonth));
    }

    @GetMapping({"/rooms", "/by-room"})
    public ResponseEntity<Page<RoomRevenueResponse>> getRevenueByRoom(
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) String q,
            Pageable pageable) {
        return ResponseEntity.ok(adminRevenueService.getRevenueByRoom(yearMonth, q, pageable));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> getRevenueCsv(@RequestParam(required = false) String yearMonth) {
        byte[] csvData = adminRevenueService.exportRevenueCsv(yearMonth);
        String filename = "revenue_export.csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(csvData);
    }
}

