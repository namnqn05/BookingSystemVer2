package com.example.booking_system.dto.response;

import com.example.booking_system.model.Room;

public class RoomResponse {

    private Long id;
    private String name;
    private Integer capacity;
    private Boolean isActive;

    public RoomResponse() {
    }

    public RoomResponse(Long id, String name, Integer capacity, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.isActive = isActive;
    }

    public static RoomResponse fromEntity(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getIsActive()
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
}
