package com.example.booking_system.dto.response;

import com.example.booking_system.model.Department;
import com.example.booking_system.model.DepartmentChangeRequestStatus;

import java.time.Instant;

public class DepartmentChangeRequestResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private Department currentDepartment;
    private Department requestedDepartment;
    private DepartmentChangeRequestStatus status;
    private Long reviewedById;
    private Instant reviewedDate;
    private Instant createdDate;

    public DepartmentChangeRequestResponse() {
    }

    public DepartmentChangeRequestResponse(Long id, Long userId, String userEmail, String userFullName,
                                            Department currentDepartment, Department requestedDepartment,
                                            DepartmentChangeRequestStatus status, Long reviewedById,
                                            Instant reviewedDate, Instant createdDate) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.currentDepartment = currentDepartment;
        this.requestedDepartment = requestedDepartment;
        this.status = status;
        this.reviewedById = reviewedById;
        this.reviewedDate = reviewedDate;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Department getCurrentDepartment() {
        return currentDepartment;
    }

    public void setCurrentDepartment(Department currentDepartment) {
        this.currentDepartment = currentDepartment;
    }

    public Department getRequestedDepartment() {
        return requestedDepartment;
    }

    public void setRequestedDepartment(Department requestedDepartment) {
        this.requestedDepartment = requestedDepartment;
    }

    public DepartmentChangeRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DepartmentChangeRequestStatus status) {
        this.status = status;
    }

    public Long getReviewedById() {
        return reviewedById;
    }

    public void setReviewedById(Long reviewedById) {
        this.reviewedById = reviewedById;
    }

    public Instant getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(Instant reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
