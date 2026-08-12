package com.example.booking_system.controller;

import com.example.booking_system.security.CustomUserDetailsService;
import com.example.booking_system.security.JwtAuthenticationFilter;
import com.example.booking_system.security.JwtTokenProvider;
import com.example.booking_system.security.PermissionAuthorizationFilter;
import com.example.booking_system.security.PermissionMapping;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

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
    void unreadNotificationCount_ReturnsUnreadCount() throws Exception {
        UserPrincipal principal = new UserPrincipal(10L, "test@example.com", "pass", "Test User", true, Collections.emptyList());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(notificationService.countUnreadNotifications(10L)).thenReturn(3L);

        mockMvc.perform(get("/api/notifications/unread-count")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        verify(notificationService).countUnreadNotifications(10L);
    }
}
