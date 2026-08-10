package com.example.booking_system.dto.response;

import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Booking;
import com.example.booking_system.model.Room;
import com.example.booking_system.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private Long roomId;
    private String roomName;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private BigDecimal pricePerHour;
    private BigDecimal amount;

    public BookingResponse() {}

    public static BookingResponse fromEntity(Booking booking, Room room, User user) {
        BookingResponse res = new BookingResponse();
        res.setId(booking.getId());
        res.setRoomId(booking.getRoomId());
        res.setRoomName(room != null ? room.getName() : null);
        res.setUserId(booking.getUserId());
        res.setUserFullName(user != null ? user.getFullName() : null);
        res.setUserEmail(user != null ? user.getEmail() : null);
        res.setTitle(booking.getTitle());
        res.setStartTime(booking.getStartTime());
        res.setEndTime(booking.getEndTime());
        res.setStatus(booking.getStatus());
        res.setPricePerHour(booking.getPricePerHour());
        res.setAmount(booking.getAmount());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
