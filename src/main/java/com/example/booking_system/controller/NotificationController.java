package com.example.booking_system.controller;

import com.example.booking_system.dto.response.NotificationResponse;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUserNotifications(principal.getId(), pageable));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> setNotificationRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.setNotificationRead(id, principal.getId()));
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> setAllNotificationRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.setAllNotificationRead(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
