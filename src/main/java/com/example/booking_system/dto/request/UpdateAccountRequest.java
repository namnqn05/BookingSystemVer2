package com.example.booking_system.dto.request;

public class UpdateAccountRequest {
    private String fullName;
    private String email;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
