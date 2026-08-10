package com.example.booking_system.service.impl;

import com.example.booking_system.dto.response.NotificationResponse;
import com.example.booking_system.model.Notification;
import com.example.booking_system.repository.NotificationRepository;
import com.example.booking_system.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientUserId(userId, pageable)
                .map(NotificationResponse::fromEntity);
    }

    @Override
    @Transactional
    public NotificationResponse setNotificationRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));

        if (!notification.getRecipientUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to mark this notification as read");
        }

        notification.setRead(true);
        notification.setReadAt(Instant.now());
        Notification updated = notificationRepository.save(notification);

        return NotificationResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void setAllNotificationRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId, Instant.now());
    }
}
