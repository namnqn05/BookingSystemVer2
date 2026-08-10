package com.example.booking_system.service;

import com.example.booking_system.dto.request.CreateDepartmentChangeRequest;
import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;

public interface UserDepartmentChangeRequestService {
    DepartmentChangeRequestResponse getUserPendingDepartmentChangeRequest(Long userId);
    DepartmentChangeRequestResponse requestDepartmentChange(Long userId, CreateDepartmentChangeRequest request);
}
