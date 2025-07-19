package com.finguard.userservice.controller;


import com.finguard.userservice.dto.UserRegistrationRequest;
import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

   // @Autowired // instead of final can use this for field injection
    // private  UserRepository userRepository;

    /**
     * The repository for performing CRUD operations on User entities.
     */
    private final UserRepository userRepository;

    /**
     * Encoder for hashing user passwords securely.
     */
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor injection for UserRepository and BCryptPasswordEncoder.
     *
     * @param userRepository    Repository for User entities.
     * @param passwordEncoder   Encoder for secure password hashing.
     */
    @Autowired // this is construction injection
    public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public String registerUser(@Valid @RequestBody UserRegistrationRequest request){
        // check if email already exists
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            return "Email already registered.";
        }

        User user = new User();
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoles("USER");

        userRepository.save(user);

        return "User registered successfully!";

    }
}
