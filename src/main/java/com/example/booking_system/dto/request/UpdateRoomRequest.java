package com.example.booking_system.dto.request;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class UpdateRoomRequest {

    private String name;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Boolean isActive;

    private BigDecimal pricePerHour;

    public UpdateRoomRequest() {
    }

    public UpdateRoomRequest(String name, Integer capacity, Boolean isActive) {
        this.name = name;
        this.capacity = capacity;
        this.isActive = isActive;
    }

    public UpdateRoomRequest(String name, Integer capacity, Boolean isActive, BigDecimal pricePerHour) {
        this.name = name;
        this.capacity = capacity;
        this.isActive = isActive;
        this.pricePerHour = pricePerHour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
}
