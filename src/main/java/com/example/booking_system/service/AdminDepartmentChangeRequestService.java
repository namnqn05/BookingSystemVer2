package com.example.booking_system.service;

import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.model.DepartmentChangeRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminDepartmentChangeRequestService {
    Page<DepartmentChangeRequestResponse> getRequests(DepartmentChangeRequestStatus status, Pageable pageable);
    DepartmentChangeRequestResponse approveRequest(Long id, Long adminUserId);
    DepartmentChangeRequestResponse rejectRequest(Long id, Long adminUserId);
}
