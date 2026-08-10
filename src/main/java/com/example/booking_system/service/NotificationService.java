package com.example.booking_system.service;

import com.example.booking_system.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);
    NotificationResponse setNotificationRead(Long id, Long userId);
    void setAllNotificationRead(Long userId);
}
