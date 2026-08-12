package com.example.booking_system.dto.response;

import com.example.booking_system.model.Room;
import java.math.BigDecimal;

public class RoomResponse {

    private Long id;
    private String name;
    private Integer capacity;
    private Boolean isActive;
    private BigDecimal pricePerHour;
    private Long lockedDepartmentId;

    public RoomResponse() {
    }

    public RoomResponse(Long id, String name, Integer capacity, Boolean isActive) {
        this(id, name, capacity, isActive, BigDecimal.ZERO, null);
    }

    public RoomResponse(Long id, String name, Integer capacity, Boolean isActive, BigDecimal pricePerHour) {
        this(id, name, capacity, isActive, pricePerHour, null);
    }

    public RoomResponse(Long id, String name, Integer capacity, Boolean isActive, BigDecimal pricePerHour, Long lockedDepartmentId) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.isActive = isActive;
        this.pricePerHour = pricePerHour;
        this.lockedDepartmentId = lockedDepartmentId;
    }

    public static RoomResponse fromEntity(Room room) {
        if (room == null) {
            return null;
        }
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getIsActive(),
                room.getPricePerHour(),
                room.getLockedDepartmentId()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getLockedDepartmentId() {
        return lockedDepartmentId;
    }

    public void setLockedDepartmentId(Long lockedDepartmentId) {
        this.lockedDepartmentId = lockedDepartmentId;
    }
}
