package com.finguard.userservice.controller;

import com.finguard.userservice.dto.LoginRequest;
import com.finguard.userservice.dto.LoginResponse;
import com.finguard.userservice.dto.UserRegistrationRequest;
import com.finguard.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

    /**
     * UserController handles all user-related HTTP requests.
     * - Exposes REST endpoints under the /api path.
     * - Handles user registration and persists user data.
     * - Encrypts passwords before saving to the database.
     * Annotations:
     *   {@code @RestController} - Indicates this class is a REST API controller.
     *   {@code @RequestMapping("/api")} - Base path for all endpoints in this controller.
     */
    @RestController
    @RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     * Constructor injection for dependencies.
     *
     * @param userService service coordinating user operations.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

        /**
         * Registers a new user.
         * - Accepts a POST request to {@code /api/register}.
         * - Expects a JSON payload mapped to {@link UserRegistrationRequest}.
         * - Validates input fields using JSR-303 annotations.
         * - Checks if the email is already registered.
         * - Encrypts the password before saving the user.
         * - Returns a success message or a warning if the email already exists.
         *
         * @param request the registration request payload containing user details.
         * @return a confirmation message as a plain text response.
         */

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok("User Registered Successfully!");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }

    }

        /**
         * Authenticates a user and returns a JWT token if successful.
        */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest){

        try{
            String token = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        }catch ( BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid Email or Password"));
        }

    }

        /**
         * A secured endpoint only accessible to authenticated users.
         *
         * @return a message showing secure data
         */
        @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint() {
        return ResponseEntity.ok("You have accessed a secured endpoint!");
    }
}
