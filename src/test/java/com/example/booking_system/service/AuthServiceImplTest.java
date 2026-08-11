package com.example.booking_system.service;

import com.example.booking_system.dto.request.LoginRequest;
import com.example.booking_system.dto.request.RegisterRequest;
import com.example.booking_system.dto.response.AuthResponse;
import com.example.booking_system.model.Role;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.security.JwtTokenProvider;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFullName("Test User");
        user.setRole(Role.ROLE_USER);
        user.setActivated(true);
        userPrincipal = UserPrincipal.create(user);
    }

    @Test
    void register_SuccessfulRegistration() {
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password123", "Test User");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        assertEquals(Role.ROLE_USER, response.getRole());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("test@example.com", userCaptor.getValue().getEmail());
        assertEquals("encodedPassword", userCaptor.getValue().getPassword());
        assertEquals(Role.ROLE_USER, userCaptor.getValue().getRole());
        assertTrue(userCaptor.getValue().isActivated());
        assertEquals("test@example.com", userCaptor.getValue().getCreatedBy());
        assertNotNull(userCaptor.getValue().getCreatedDate());
        assertEquals("test@example.com", userCaptor.getValue().getLastModifiedBy());
        assertNotNull(userCaptor.getValue().getLastModifiedDate());
    }

    @Test
    void register_AlwaysAssignsUserRole() {
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password123", "Test User");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(Role.ROLE_USER, userCaptor.getValue().getRole());
        assertTrue(userCaptor.getValue().isActivated());
    }

    @Test
    void register_DuplicateEmailThrowsException() {
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password123", "Test User");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Email is already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
