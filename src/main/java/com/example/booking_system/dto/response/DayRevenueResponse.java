package com.example.booking_system.dto.response;

import java.math.BigDecimal;

public class DayRevenueResponse {

    private String date;
    private Integer bookingCount;
    private BigDecimal amount;

    public DayRevenueResponse() {
    }

    public DayRevenueResponse(String date, Integer bookingCount, BigDecimal amount) {
        this.date = date;
        this.bookingCount = bookingCount;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
