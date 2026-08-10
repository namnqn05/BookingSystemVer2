package com.example.booking_system.controller;

import com.example.booking_system.dto.request.CreateDepartmentChangeRequest;
import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.UserDepartmentChangeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/department-change-requests")
public class DepartmentChangeRequestController {

    private final UserDepartmentChangeRequestService userDepartmentChangeRequestService;
    private final UserRepository userRepository;

    public DepartmentChangeRequestController(
            UserDepartmentChangeRequestService userDepartmentChangeRequestService,
            UserRepository userRepository) {
        this.userDepartmentChangeRequestService = userDepartmentChangeRequestService;
        this.userRepository = userRepository;
    }

    @GetMapping("/pending")
    public ResponseEntity<DepartmentChangeRequestResponse> getUserPendingDepartmentChangeRequest() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        DepartmentChangeRequestResponse response = userDepartmentChangeRequestService.getUserPendingDepartmentChangeRequest(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DepartmentChangeRequestResponse> requestDepartmentChange(@RequestBody CreateDepartmentChangeRequest request) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        DepartmentChangeRequestResponse response = userDepartmentChangeRequestService.requestDepartmentChange(userId, request);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername()).map(User::getId).orElse(null);
        }
        return null;
    }
}
