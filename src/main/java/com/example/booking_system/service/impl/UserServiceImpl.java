package com.example.booking_system.service.impl;

import com.example.booking_system.dto.request.CreateUserRequest;
import com.example.booking_system.dto.request.UpdateUserRequest;
import com.example.booking_system.dto.response.UserResponse;
import com.example.booking_system.model.Department;
import com.example.booking_system.model.Role;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.DepartmentRepository;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UserResponse> getAllUsers(String q, Boolean activated, Pageable pageable) {
        return userRepository.findAllUsersWithPaginationAndSearch(q, activated, pageable)
                .map(UserResponse::fromEntity);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() != null ? request.getRole() : Role.ROLE_USER);
        user.setActivated(request.getActivated() != null ? request.getActivated() : false);
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            user.setDepartment(department);
        }

        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActivated() != null) {
            user.setActivated(request.getActivated());
        }
        
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            user.setDepartment(department);
        } else {
            user.setDepartment(null);
        }

        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        user.setActivated(false);
        userRepository.save(user);
    }
}
