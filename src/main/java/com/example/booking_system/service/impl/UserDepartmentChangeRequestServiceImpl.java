package com.example.booking_system.service.impl;

import com.example.booking_system.dto.request.CreateDepartmentChangeRequest;
import com.example.booking_system.dto.response.DepartmentChangeRequestResponse;
import com.example.booking_system.model.Department;
import com.example.booking_system.model.DepartmentChangeRequest;
import com.example.booking_system.model.DepartmentChangeRequestStatus;
import com.example.booking_system.model.Notification;
import com.example.booking_system.model.NotificationType;
import com.example.booking_system.model.Role;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.DepartmentChangeRequestRepository;
import com.example.booking_system.repository.DepartmentRepository;
import com.example.booking_system.repository.NotificationRepository;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.service.UserDepartmentChangeRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserDepartmentChangeRequestServiceImpl implements UserDepartmentChangeRequestService {

    private final DepartmentChangeRequestRepository departmentChangeRequestRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final NotificationRepository notificationRepository;

    public UserDepartmentChangeRequestServiceImpl(
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
    public DepartmentChangeRequestResponse getUserPendingDepartmentChangeRequest(Long userId) {
        List<DepartmentChangeRequest> pendingRequests = departmentChangeRequestRepository
                .findByUserIdAndStatus(userId, DepartmentChangeRequestStatus.PENDING);

        if (pendingRequests.size() > 1) {
            throw new IllegalStateException("Multiple pending department change requests found for user with id: " + userId);
        }

        if (pendingRequests.isEmpty()) {
            return null;
        }

        return mapToResponse(pendingRequests.get(0));
    }

    @Override
    @Transactional
    public DepartmentChangeRequestResponse requestDepartmentChange(Long userId, CreateDepartmentChangeRequest requestDto) {
        Long targetDeptId = requestDto.getRequestedDepartmentId();
        if (targetDeptId == null) {
            throw new IllegalArgumentException("Requested department ID must be provided");
        }

        Department targetDept = departmentRepository.findById(targetDeptId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + targetDeptId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (user.getDepartment() != null && user.getDepartment().getId().equals(targetDeptId)) {
            throw new IllegalArgumentException("User is already in the requested department");
        }

        List<DepartmentChangeRequest> pendingRequests = departmentChangeRequestRepository
                .findByUserIdAndStatus(userId, DepartmentChangeRequestStatus.PENDING);

        if (pendingRequests.size() > 1) {
            throw new IllegalStateException("Multiple pending department change requests found for user with id: " + userId);
        }

        if (!pendingRequests.isEmpty()) {
            throw new IllegalArgumentException("User already has a pending department change request");
        }

        DepartmentChangeRequest changeRequest = new DepartmentChangeRequest();
        changeRequest.setUserId(userId);
        changeRequest.setRequestedDepartmentId(targetDeptId);
        changeRequest.setStatus(DepartmentChangeRequestStatus.PENDING);
        changeRequest.setCreatedBy(user.getEmail() != null ? user.getEmail() : "user_" + userId);
        changeRequest.setCreatedDate(Instant.now());

        DepartmentChangeRequest saved = departmentChangeRequestRepository.save(changeRequest);

        List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
        for (User admin : admins) {
            notificationRepository.save(Notification.create(
                    admin,
                    "New Department Change Request",
                    "User " + (user.getFullName() != null ? user.getFullName() : user.getEmail())
                            + " requested to change department to " + targetDept.getName() + ".",
                    NotificationType.DEPT_CHANGE_PENDING,
                    "DEPARTMENT_CHANGE_REQUEST",
                    saved.getId(),
                    user.getId()
            ));
        }

        return mapToResponse(saved);
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
