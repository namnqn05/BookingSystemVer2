package com.example.booking_system.dto.response;

import com.example.booking_system.model.Department;
import com.example.booking_system.model.User;

import java.util.List;

public class AccountResponse {

    private Long id;
    private String email;
    private String fullName;
    private boolean activated;
    private List<String> role;
    private Department department;

    public AccountResponse() {
    }

    public AccountResponse(Long id, String email, String fullName, boolean activated, List<String> role, Department department) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.activated = activated;
        this.role = role;
        this.department = department;
    }

    public static AccountResponse fromEntity(User user) {
        return new AccountResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isActivated(),
                List.of(user.getRole().name()),
                user.getDepartment()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public boolean isActivated() { return activated; }
    public void setActivated(boolean activated) { this.activated = activated; }

    public List<String> getRole() { return role; }
    public void setRole(List<String> role) { this.role = role; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
