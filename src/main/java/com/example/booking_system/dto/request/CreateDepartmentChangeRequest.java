package com.example.booking_system.dto.request;

public class CreateDepartmentChangeRequest {

    private Long requestedDepartmentId;

    public CreateDepartmentChangeRequest() {
    }

    public CreateDepartmentChangeRequest(Long requestedDepartmentId) {
        this.requestedDepartmentId = requestedDepartmentId;
    }

    public Long getRequestedDepartmentId() {
        return requestedDepartmentId;
    }

    public void setRequestedDepartmentId(Long requestedDepartmentId) {
        this.requestedDepartmentId = requestedDepartmentId;
    }
}
