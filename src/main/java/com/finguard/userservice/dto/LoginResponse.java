package com.finguard.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO used to send the JWT token response after successful login.
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
