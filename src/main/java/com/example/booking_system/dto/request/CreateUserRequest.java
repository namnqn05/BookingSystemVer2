package com.example.booking_system.dto.request;

import com.example.booking_system.model.Role;

public class CreateUserRequest {
    private String email;
    private String password;
    private String fullName;
    private Role role;
    private Boolean activated;
    private Long departmentId;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getActivated() { return activated; }
    public void setActivated(Boolean activated) { this.activated = activated; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}
