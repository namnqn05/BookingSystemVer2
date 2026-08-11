package com.example.booking_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateRoomRequest {

    @NotBlank(message = "Room name is required")
    private String name;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Boolean isActive = true;

    private BigDecimal pricePerHour;

    public CreateRoomRequest() {
    }

    public CreateRoomRequest(String name, Integer capacity, Boolean isActive) {
        this.name = name;
        this.capacity = capacity;
        this.isActive = isActive != null ? isActive : true;
    }

    public CreateRoomRequest(String name, Integer capacity, Boolean isActive, BigDecimal pricePerHour) {
        this.name = name;
        this.capacity = capacity;
        this.isActive = isActive != null ? isActive : true;
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
