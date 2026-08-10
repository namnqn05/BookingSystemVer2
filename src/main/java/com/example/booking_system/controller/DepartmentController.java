package com.example.booking_system.controller;

import com.example.booking_system.model.Department;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserRepository userRepository;

    public DepartmentController(DepartmentService departmentService, UserRepository userRepository) {
        this.departmentService = departmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<Department> getUserDepartment() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Department department = departmentService.getUserDepartment(userId);
        return ResponseEntity.ok(department);
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
