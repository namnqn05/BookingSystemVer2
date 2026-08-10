package com.example.booking_system.service;

import com.example.booking_system.dto.request.LoginRequest;
import com.example.booking_system.dto.request.RegisterRequest;
import com.example.booking_system.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
}

