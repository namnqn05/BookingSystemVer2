package com.example.booking_system.service.impl;

import com.example.booking_system.model.Department;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final UserRepository userRepository;

    public DepartmentServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Department getUserDepartment(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return user.getDepartment();
    }
}
