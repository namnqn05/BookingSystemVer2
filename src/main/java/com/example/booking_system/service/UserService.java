package com.example.booking_system.service;

import com.example.booking_system.dto.request.CreateUserRequest;
import com.example.booking_system.dto.request.UpdateUserRequest;
import com.example.booking_system.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(String q, Boolean activated, Pageable pageable);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deactivateUser(Long id);
}
