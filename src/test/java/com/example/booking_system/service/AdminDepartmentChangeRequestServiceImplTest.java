package com.example.booking_system.service;

import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.model.Department;
import com.example.booking_system.model.DepartmentChangeRequest;
import com.example.booking_system.model.DepartmentChangeRequestStatus;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.DepartmentChangeRequestRepository;
import com.example.booking_system.repository.DepartmentRepository;
import com.example.booking_system.repository.NotificationRepository;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.service.impl.AdminDepartmentChangeRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDepartmentChangeRequestServiceImplTest {

    @Mock
    private DepartmentChangeRequestRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private AdminDepartmentChangeRequestServiceImpl service;

    private DepartmentChangeRequest pendingRequest;

    @BeforeEach
    void setUp() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("IT");

        pendingRequest = new DepartmentChangeRequest();
        pendingRequest.setId(1L);
        pendingRequest.setUserId(3L);
        pendingRequest.setRequestedDepartment(dept);
        pendingRequest.setStatus(DepartmentChangeRequestStatus.PENDING);
    }

    @Test
    void getRequests_WithNullStatus_ReturnsAllRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentChangeRequest> page = new PageImpl<>(List.of(pendingRequest));

        when(repository.findAll(pageable)).thenReturn(page);

        Page<DepartmentChangeRequestResponse> result = service.getRequests(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(pageable);
    }

    @Test
    void getRequests_WithPendingStatus_ReturnsPendingRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentChangeRequest> page = new PageImpl<>(List.of(pendingRequest));

        when(repository.findByStatus(DepartmentChangeRequestStatus.PENDING, pageable)).thenReturn(page);

        Page<DepartmentChangeRequestResponse> result = service.getRequests(DepartmentChangeRequestStatus.PENDING, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(DepartmentChangeRequestStatus.PENDING, result.getContent().get(0).getStatus());
        verify(repository).findByStatus(DepartmentChangeRequestStatus.PENDING, pageable);
    }
}
