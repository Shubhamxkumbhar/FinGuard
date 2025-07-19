package com.finguard.userservice.service;


import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for business logic related to user operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Handles user registration.</li>
 *   <li>Encapsulates password hashing logic via BCrypt.</li>
 *   <li>Performs user lookups and validations.</li>
 * </ul>
 *
 * <p>Benefits of using a Service layer:
 * <ul>
 *   <li>Separates business logic from controllers.</li>
 *   <li>Enables better testing and maintainability.</li>
 *   <li>Follows the principles of Clean Architecture.</li>
 * </ul>
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;  // Provides methods to hash and check passwords.

    // Constructor Injection (recommended over @Autowired field injection)
    /**
     * Constructor-based dependency injection.
     *
     * @param userRepository Repository for user persistence.
     * @param passwordEncoder BCrypt encoder for secure password hashing.
     */
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system.
     *
     * <p>This method:
     * <ul>
     *   <li>Encodes the user's plain-text password using BCrypt.</li>
     *   <li>Saves the user entity to the database.</li>
     * </ul>
     *
     * @param user the user entity containing registration details.
     * @return the saved user entity.
     */
    public User registerUser(User user){
        //Hash the Password
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by email address.
     *
     * @param email the email address to search for.
     * @return Optional containing the user, if found.
     */
    public Optional<User> findByEmail(String email){

        return userRepository.findByEmail(email);
    }

    /**
     * Checks if a user already exists with the provided email.
     *
     * @param email the email to check.
     * @return true if the email is already registered, false otherwise.
     */
    public boolean emailExists(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
