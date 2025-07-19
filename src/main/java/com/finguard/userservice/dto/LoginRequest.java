package com.finguard.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for login credentials.
 * Contains email and password fields used in /api/login endpoint.
 */
@Getter
@Setter
public class LoginRequest {

    @Email(message = "Invalid Email format")
    @NotBlank(message = "Email is required")
    public String email;

    @NotBlank(message = "Password is required")
    public String password;
}
