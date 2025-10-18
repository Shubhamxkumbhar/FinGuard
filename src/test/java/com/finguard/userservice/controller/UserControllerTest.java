package com.finguard.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finguard.userservice.dto.LoginRequest;
import com.finguard.userservice.dto.UserRegistrationRequest;
import com.finguard.userservice.model.User;
import com.finguard.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.finguard.userservice.UserServiceApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/register - success")
    void registerUser_success() throws Exception {
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(new User());

        UserRegistrationRequest request = new UserRegistrationRequest(
                "Shubham",
                "shubham@gmail.com",
                "Password1",
                List.of("USER")
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registered Successfully!"));

        verify(userService).registerUser(any(UserRegistrationRequest.class));
    }

    @Test
    @DisplayName("POST /api/register - duplicate email returns 409")
    void registerUser_duplicateEmail() throws Exception {
        doThrow(new IllegalStateException("Email already registered.")).when(userService)
                .registerUser(any(UserRegistrationRequest.class));

        UserRegistrationRequest request = new UserRegistrationRequest(
                "Shubham",
                "taken@example.com",
                "Password1",
                List.of("USER")
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already registered."));
    }

    @Test
    @DisplayName("POST /api/register - validation errors return structured response")
    void registerUser_invalidPayload() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                " ",
                "invalid",
                "weak",
                List.of(" ")
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    @Test
    @DisplayName("POST /api/login - success")
    void loginUser_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("shubham@gmail.com", "Password1");
        when(userService.login("shubham@gmail.com", "Password1")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("POST /api/login - invalid credentials")
    void loginUser_invalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("shubham@gmail.com", "wrong");
        when(userService.login("shubham@gmail.com", "wrong"))
                .thenThrow(new BadCredentialsException("Invalid Email or Password"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid Email or Password"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/secure - returns secured message")
    void secureEndpoint_accessGranted() throws Exception {
        mockMvc.perform(get("/api/secure"))
                .andExpect(status().isOk())
                .andExpect(content().string("You have accessed a secured endpoint!"));
    }

    @Test
    @DisplayName("POST /api/register - captures requested roles")
    void registerUser_capturesRoles() throws Exception {
        User savedUser = new User();
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(savedUser);

        UserRegistrationRequest request = new UserRegistrationRequest(
                "Shubham",
                "multi@example.com",
                "Password1",
                List.of("USER", "ADMIN")
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ArgumentCaptor<UserRegistrationRequest> captor = ArgumentCaptor.forClass(UserRegistrationRequest.class);
        verify(userService, times(1)).registerUser(captor.capture());
        UserRegistrationRequest captured = captor.getValue();

        assertEquals(List.of("USER", "ADMIN"), captured.getRoles());
    }
}
