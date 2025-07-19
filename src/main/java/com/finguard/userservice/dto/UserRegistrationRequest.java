package com.finguard.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO for user registration request payload.
 * Maps JSON input from clients into a Java object.
 * Includes validation rules for incoming fields.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    /**
     * User's full name.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * User's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    /**
     * User's password.
     */
    @NotBlank(message = "Password is required")
    @Size(min=8 , max= 20,  message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    private String password;

}
