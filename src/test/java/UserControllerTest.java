import com.finguard.userservice.controller.UserController;
import com.finguard.userservice.dto.LoginRequest;

import com.finguard.userservice.dto.UserRegistrationRequest;
import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import com.finguard.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; //  Used to mock dependencies in Spring Context
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Integration tests for {@link UserController}.
 * Uses @WebMvcTest to initialize the Spring MVC layer and mock the rest.
 * Focuses on testing registration and login endpoints for correct HTTP behavior.
 */
//@WebMvcTest(UserController.class)// Loads only the controller layer, excluding service/repo beans unless mocked

// @SpringBootTest : Loads the full Spring application context for integration testing, using the main application class as configuration.
@SpringBootTest(classes = com.finguard.userservice.UserServiceApplication.class)
@AutoConfigureMockMvc // Auto-configures MockMvc instance for testing MVC controllers without starting the server.
public class UserControllerTest {

    /**
     * MockMvc is a Spring test tool for simulating HTTP requests in tests.
     * It's autoconfigured by @WebMvcTest and injected here.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock the UserService so controller logic can be tested without invoking real service logic.
     */
    @MockBean       // Creates a mock and registers it in the Spring context to be injected where needed
    private UserService userService;

    /**
     * ObjectMapper is used to convert Java objects to JSON and vice versa.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mocks the BCryptPasswordEncoder to avoid actual hashing.
     */
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Mocks the UserRepository so we don’t hit a real database.
     */
    @MockBean
    private UserRepository userRepository;


    @Test
    @DisplayName("POST /api/register - Success") // Gives a human-readable name to the test in test reports. Optional but helpful for clarity.
    void testRegisterUser_success() throws Exception{
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
          "Shubham", "shubham@gmail.com", "Shubham@123890", Collections.singletonList("USER")
        );

        when(userRepository.findByEmail("shubham@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Shubham@123890")).thenReturn("hashedPwd");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registered Successfully!"));
    }

    /**
     * Tests that invalid registration payload (e.g., missing fields) returns 400 Bad Request.
     */
    @Test
    @DisplayName("Register: Should return 400 for invalid payload")
    void testRegister_invalidPayload() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(" ", "not-an-email", "weak", Collections.singletonList(" "));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()").value(3)); // Expecting 3 validation errors
    }

    /**
     * Tests that a successful login returns a 200 OK with the expected JWT token in response body.
     */
    @Test
    @DisplayName("POST /api/login - Login : return 200 OK and JWT token")
    void testLoginUser_success() throws Exception{
        LoginRequest loginRequest = new LoginRequest("shubham@gmail.com", "Shubham@123890");

        Mockito.when(userService.login("shubham@gmail.com", "Shubham@123890"))
                .thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    /**
     * Tests that invalid credentials return a 401 Unauthorized with a proper error message.
     */
    @Test
    @DisplayName("POST /api/login - Login: return 401 if credentials are invalid")
    void testLoginRequest_invalidCredentials() throws Exception{
        LoginRequest loginRequest = new LoginRequest("shubham@gmail.com", "wrongPass");

        Mockito.when(userService.login("shubham@gmail.com","wrongPass" ))
                .thenThrow(new BadCredentialsException("Invalid Email or Password"));

        mockMvc.perform(post("/api/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid Email or Password"));
    }

    /**
     * Test for the /api/secure endpoint.
     * (This is a dummy test assuming authentication is successful).
     */
    @Test
    @WithMockUser
    @DisplayName("GET /api/secure - Should return secured message")
    void testSecureEndpoint_AccessGranted() throws Exception {
        mockMvc.perform(get("/api/secure"))
                .andExpect(status().isOk())
                .andExpect(content().string("You have accessed a secured endpoint!"));
    }

    @Test
    @DisplayName("POST /api/register - Success with Multiple Roles")
    void testRegisterUser_successMultipleRoles() throws Exception {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Shubham Kumbhar",
                "shubham@gmail.com",
                "Shubham@123890",
                Arrays.asList("USER", "ADMIN")
        );

        // Mock email check returns empty (no user exists)
        when(userRepository.findByEmail("shubham@gmail.com")).thenReturn(Optional.empty());
        // Mock password encoder hashes password
        when(passwordEncoder.encode("Shubham@123890")).thenReturn("hashedStrongPass");
        // Mock saving user returns user with roles set
        Mockito.doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            assertNotNull(u);
            // Assert roles contains both USER and ADMIN
            assertTrue(u.getRoles().contains("USER"));
            assertTrue(u.getRoles().contains("ADMIN"));
            return u;
        }).when(userRepository).save(any(User.class));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registered Successfully!"));

        // Verify repository methods were called
        verify(userRepository).findByEmail("shubham@gmail.com");
        verify(passwordEncoder).encode("Shubham@123890");
        verify(userRepository).save(any(User.class));
    }



}
