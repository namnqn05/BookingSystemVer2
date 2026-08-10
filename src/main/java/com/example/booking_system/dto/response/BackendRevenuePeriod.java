package com.example.booking_system.dto.response;

import java.math.BigDecimal;

public class BackendRevenuePeriod {

    private String yearMonth;
    private BigDecimal totalAmount;
    private Integer totalBookings;
    private BigDecimal averageAmount;
    private Integer cancelledCount;
    private BigDecimal cancellationRate;

    public BackendRevenuePeriod() {
    }

    public BackendRevenuePeriod(String yearMonth, BigDecimal totalAmount, Integer totalBookings,
                                BigDecimal averageAmount, Integer cancelledCount, BigDecimal cancellationRate) {
        this.yearMonth = yearMonth;
        this.totalAmount = totalAmount;
        this.totalBookings = totalBookings;
        this.averageAmount = averageAmount;
        this.cancelledCount = cancelledCount;
        this.cancellationRate = cancellationRate;
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
}
