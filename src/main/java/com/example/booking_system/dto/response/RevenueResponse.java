package com.example.booking_system.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class RevenueResponse {

    private String yearMonth;
    private BigDecimal totalAmount;
    private Integer totalBookings;
    private BigDecimal averageAmount;
    private Integer cancelledCount;
    private BigDecimal cancellationRate;
    private BackendRevenuePeriod previous;
    private List<RoomRevenueResponse> byRoom;
    private List<DayRevenueResponse> byDay;

    public RevenueResponse() {
    }

    public RevenueResponse(String yearMonth, BigDecimal totalAmount, Integer totalBookings,
                           BigDecimal averageAmount, Integer cancelledCount, BigDecimal cancellationRate,
                           BackendRevenuePeriod previous, List<RoomRevenueResponse> byRoom,
                           List<DayRevenueResponse> byDay) {
        this.yearMonth = yearMonth;
        this.totalAmount = totalAmount;
        this.totalBookings = totalBookings;
        this.averageAmount = averageAmount;
        this.cancelledCount = cancelledCount;
        this.cancellationRate = cancellationRate;
        this.previous = previous;
        this.byRoom = byRoom;
        this.byDay = byDay;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Integer totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }

    public Integer getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(Integer cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public BigDecimal getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(BigDecimal cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public BackendRevenuePeriod getPrevious() {
        return previous;
    }

    public void setPrevious(BackendRevenuePeriod previous) {
        this.previous = previous;
    }

    public List<RoomRevenueResponse> getByRoom() {
        return byRoom;
    }

    public void setByRoom(List<RoomRevenueResponse> byRoom) {
        this.byRoom = byRoom;
    }

    public List<DayRevenueResponse> getByDay() {
        return byDay;
    }

    public void setByDay(List<DayRevenueResponse> byDay) {
        this.byDay = byDay;
    }
}
