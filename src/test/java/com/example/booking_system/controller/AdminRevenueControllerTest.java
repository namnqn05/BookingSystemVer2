package com.example.booking_system.controller;

import com.example.booking_system.dto.response.RevenueResponse;
import com.example.booking_system.security.CustomUserDetailsService;
import com.example.booking_system.security.JwtAuthenticationFilter;
import com.example.booking_system.security.JwtTokenProvider;
import com.example.booking_system.security.PermissionAuthorizationFilter;
import com.example.booking_system.security.PermissionMapping;
import com.example.booking_system.service.AdminRevenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminRevenueController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminRevenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminRevenueService adminRevenueService;

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
    void getRevenue_ReturnsOk() throws Exception {
        when(adminRevenueService.getRevenue(anyString())).thenReturn(new RevenueResponse());

        mockMvc.perform(get("/api/admin/revenue").param("yearMonth", "2026-08"))
                .andExpect(status().isOk());
    }

    @Test
    void getRevenueCsv_ReturnsCsvFile() throws Exception {
        byte[] sampleCsv = "Year/Month,Total Amount\n2026-08,1000.00".getBytes(StandardCharsets.UTF_8);
        when(adminRevenueService.exportRevenueCsv("2026-08")).thenReturn(sampleCsv);

        mockMvc.perform(get("/api/admin/revenue/export").param("yearMonth", "2026-08"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"revenue_export.csv\""))
                .andExpect(content().bytes(sampleCsv));
    }
}
