package com.example.booking_system.dto.response;

import java.math.BigDecimal;

public class RoomRevenueResponse {

    private Long roomId;
    private String roomName;
    private Integer bookingCount;
    private BigDecimal amount;
    private BigDecimal sharePercent;

    public RoomRevenueResponse() {
    }

    public RoomRevenueResponse(Long roomId, String roomName, Integer bookingCount, BigDecimal amount, BigDecimal sharePercent) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.bookingCount = bookingCount;
        this.amount = amount;
        this.sharePercent = sharePercent;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getSharePercent() {
        return sharePercent;
    }

    public void setSharePercent(BigDecimal sharePercent) {
        this.sharePercent = sharePercent;
    }
}
