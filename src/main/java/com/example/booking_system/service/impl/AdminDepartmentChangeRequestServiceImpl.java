package com.example.booking_system.service.impl;

import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.model.Department;
import com.example.booking_system.model.DepartmentChangeRequest;
import com.example.booking_system.model.DepartmentChangeRequestStatus;
import com.example.booking_system.model.Notification;
import com.example.booking_system.model.NotificationType;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.DepartmentChangeRequestRepository;
import com.example.booking_system.repository.DepartmentRepository;
import com.example.booking_system.repository.NotificationRepository;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.service.AdminDepartmentChangeRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class AdminDepartmentChangeRequestServiceImpl implements AdminDepartmentChangeRequestService {

    private final DepartmentChangeRequestRepository departmentChangeRequestRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final NotificationRepository notificationRepository;

    public AdminDepartmentChangeRequestServiceImpl(
            DepartmentChangeRequestRepository departmentChangeRequestRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            NotificationRepository notificationRepository) {
        this.departmentChangeRequestRepository = departmentChangeRequestRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<DepartmentChangeRequestResponse> getRequests(DepartmentChangeRequestStatus status, Pageable pageable) {
        return departmentChangeRequestRepository.findAllByStatusWithPagination(status, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public DepartmentChangeRequestResponse approveRequest(Long id, Long adminUserId) {
        DepartmentChangeRequest request = departmentChangeRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department change request not found with id: " + id));

        request.setStatus(DepartmentChangeRequestStatus.APPROVED);
        request.setReviewedById(adminUserId);
        request.setReviewedDate(Instant.now());
        departmentChangeRequestRepository.save(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
        Department requestedDepartment = departmentRepository.findById(request.getRequestedDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + request.getRequestedDepartmentId()));

        user.setDepartment(requestedDepartment);
        userRepository.save(user);

        notificationRepository.save(Notification.create(
                user,
                "Department Change Request Approved",
                "Your request to change department to " + requestedDepartment.getName() + " has been approved.",
                NotificationType.DEPT_CHANGE_APPROVED,
                "DEPARTMENT_CHANGE_REQUEST",
                request.getId(),
                adminUserId
        ));

        return mapToResponse(request);
    }

    @Override
    @Transactional
    public DepartmentChangeRequestResponse rejectRequest(Long id, Long adminUserId) {
        DepartmentChangeRequest request = departmentChangeRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department change request not found with id: " + id));

        request.setStatus(DepartmentChangeRequestStatus.REJECTED);
        request.setReviewedById(adminUserId);
        request.setReviewedDate(Instant.now());
        departmentChangeRequestRepository.save(request);

        User user = userRepository.findById(request.getUserId()).orElse(null);
        Department requestedDepartment = departmentRepository.findById(request.getRequestedDepartmentId()).orElse(null);
        if (user != null) {
            notificationRepository.save(Notification.create(
                    user,
                    "Department Change Request Rejected",
                    "Your request to change department" + (requestedDepartment != null ? " to " + requestedDepartment.getName() : "") + " has been rejected.",
                    NotificationType.DEPT_CHANGE_REJECTED,
                    "DEPARTMENT_CHANGE_REQUEST",
                    request.getId(),
                    adminUserId
            ));
        }

        return mapToResponse(request);
    }

    private DepartmentChangeRequestResponse mapToResponse(DepartmentChangeRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        Department requestedDepartment = departmentRepository.findById(request.getRequestedDepartmentId()).orElse(null);

        String userEmail = user != null ? user.getEmail() : null;
        String userFullName = user != null ? user.getFullName() : null;
        Department currentDepartment = user != null ? user.getDepartment() : null;

        return new DepartmentChangeRequestResponse(
                request.getId(),
                request.getUserId(),
                userEmail,
                userFullName,
                currentDepartment,
                requestedDepartment,
                request.getStatus(),
                request.getReviewedById(),
                request.getReviewedDate(),
                request.getCreatedDate()
        );
    }
}
