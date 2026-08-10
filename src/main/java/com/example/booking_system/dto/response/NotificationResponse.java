package com.example.booking_system.dto.response;

import com.example.booking_system.model.Notification;
import com.example.booking_system.model.NotificationType;
import com.example.booking_system.model.User;

import java.time.Instant;

public class NotificationResponse {

    private Long id;
    private User recipientUser;
    private String title;
    private String message;
    private NotificationType type;
    private String referenceType;
    private Long referenceId;
    private boolean isRead;
    private Instant createdAt;
    private Instant readAt;
    private Long createdBy;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, User recipientUser, String title, String message,
                                NotificationType type, String referenceType, Long referenceId,
                                boolean isRead, Instant createdAt, Instant readAt, Long createdBy) {
        this.id = id;
        this.recipientUser = recipientUser;
        this.title = title;
        this.message = message;
        this.type = type;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.createdBy = createdBy;
    }

    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipientUser(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getReferenceType(),
                notification.getReferenceId(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.getCreatedBy()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
