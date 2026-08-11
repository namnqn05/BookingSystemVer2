package com.example.booking_system.controller;

import com.example.booking_system.model.Department;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/user")
    public ResponseEntity<Department> getUserDepartment(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(departmentService.getUserDepartment(principal.getId()));
    }
}
