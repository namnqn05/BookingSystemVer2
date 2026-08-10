package com.example.booking_system.controller;

import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.model.DepartmentChangeRequestStatus;
import com.example.booking_system.service.AdminDepartmentChangeRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.booking_system.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/admin/department-change-requests")
public class AdminDepartmentChangeRequestController {

    private final AdminDepartmentChangeRequestService adminDepartmentChangeRequestService;

    public AdminDepartmentChangeRequestController(AdminDepartmentChangeRequestService adminDepartmentChangeRequestService) {
        this.adminDepartmentChangeRequestService = adminDepartmentChangeRequestService;
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentChangeRequestResponse>> getRequests(
            @RequestParam(required = false) DepartmentChangeRequestStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(adminDepartmentChangeRequestService.getRequests(status, pageable));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<DepartmentChangeRequestResponse> approveRequest(@PathVariable Long id) {
        Long adminUserId = getCurrentUserId();
        return ResponseEntity.ok(adminDepartmentChangeRequestService.approveRequest(id, adminUserId));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<DepartmentChangeRequestResponse> rejectRequest(@PathVariable Long id) {
        Long adminUserId = getCurrentUserId();
        return ResponseEntity.ok(adminDepartmentChangeRequestService.rejectRequest(id, adminUserId));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        return null;
    }
}
