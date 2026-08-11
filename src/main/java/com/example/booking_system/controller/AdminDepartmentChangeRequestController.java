package com.example.booking_system.controller;

import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.model.DepartmentChangeRequestStatus;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.AdminDepartmentChangeRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/department-change-requests")
public class AdminDepartmentChangeRequestController {

    private final AdminDepartmentChangeRequestService adminDepartmentChangeRequestService;

    public AdminDepartmentChangeRequestController(
            AdminDepartmentChangeRequestService adminDepartmentChangeRequestService) {
        this.adminDepartmentChangeRequestService = adminDepartmentChangeRequestService;
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentChangeRequestResponse>> getRequests(
            @RequestParam(required = false) DepartmentChangeRequestStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(adminDepartmentChangeRequestService.getRequests(status, pageable));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<DepartmentChangeRequestResponse> approveRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(adminDepartmentChangeRequestService.approveRequest(id, principal.getId()));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<DepartmentChangeRequestResponse> rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(adminDepartmentChangeRequestService.rejectRequest(id, principal.getId()));
    }
}
