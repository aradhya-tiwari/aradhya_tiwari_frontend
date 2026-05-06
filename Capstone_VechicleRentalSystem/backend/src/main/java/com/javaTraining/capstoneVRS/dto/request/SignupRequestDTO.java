package com.javaTraining.capstoneVRS.dto.request;

import com.javaTraining.capstoneVRS.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name can be max 120 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email can be max 255 characters")
    // Email validation: (?i) for case insensitivity, [A-Z0-9._%+-] for all
    // alphabets, numbers, ., _, %, +, and -. then for the domain we have gmail.com,
    // outlook.com and nucleusTeq.com
    @Pattern(regexp = "(?i)^[A-Z0-9._%+-]+@(gmail\\.com|outlook\\.com|nucleusteq\\.com)$", message = "Email must be a valid @gmail.com or @outlook.com address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;

    private UserRole role = UserRole.USER;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
