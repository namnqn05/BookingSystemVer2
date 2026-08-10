package com.example.booking_system.dto.response;

import com.example.booking_system.model.Department;
import com.example.booking_system.model.Role;
import com.example.booking_system.model.User;

public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private boolean activated;
    private Department department;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, String fullName, Role role, boolean activated, Department department) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.activated = activated;
        this.department = department;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.isActivated(),
                user.getDepartment()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActivated() { return activated; }
    public void setActivated(boolean activated) { this.activated = activated; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
