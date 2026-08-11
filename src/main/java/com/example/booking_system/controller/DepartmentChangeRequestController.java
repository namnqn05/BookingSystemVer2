package com.example.booking_system.controller;

import com.example.booking_system.dto.request.CreateDepartmentChangeRequest;
import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.UserDepartmentChangeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/department-change-requests")
public class DepartmentChangeRequestController {

    private final UserDepartmentChangeRequestService userDepartmentChangeRequestService;

    public DepartmentChangeRequestController(
            UserDepartmentChangeRequestService userDepartmentChangeRequestService) {
        this.userDepartmentChangeRequestService = userDepartmentChangeRequestService;
    }

    @GetMapping("/pending")
    public ResponseEntity<DepartmentChangeRequestResponse> getUserPendingDepartmentChangeRequest(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                userDepartmentChangeRequestService.getUserPendingDepartmentChangeRequest(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<DepartmentChangeRequestResponse> requestDepartmentChange(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateDepartmentChangeRequest request) {
        return ResponseEntity.ok(
                userDepartmentChangeRequestService.requestDepartmentChange(principal.getId(), request));
    }
}
