package com.finguard.userservice.service;

import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import com.finguard.userservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
 *
 * UserDetailsService is used by Spring Security to fetch user information during authentication.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;  // Provides methods to hash and check passwords.
    private final JwtUtil jwtUtil;

    // Constructor Injection (recommended over @Autowired field injection)
    /**
     * Constructor-based dependency injection.
     *
     * @param userRepository Repository for user persistence.
     * @param passwordEncoder BCrypt encoder for secure password hashing.
     */
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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


    /**
     * Authenticates the user using email and password.
     * If successful, returns a LoginResponse with JWT token containing email and roles.
     * @param rawPassword :  the plain-text password entered by the user on the login form (sent in the UserLoginRequest DTO).

    public LoginResponse login(String email, String rawPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email or Password"));

        // user.getPassword(): the hashed password fetched from the database.
        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new BadCredentialsException("Invalid Email or Password");
        }

        // TODO: Fetch real roles when implemented — using hardcoded "USER" role for now
        List<String> roles = Collections.singletonList("USER");
        String token = jwtUtil.generateToken(email, roles);

        return new LoginResponse(token);
    } */

    /**
     * Authenticates a user and generates JWT upon success.
     *
     * @param email    User's email
     * @param rawPassword Raw password input
     * @return JWT token string if authentication is successful
     * @throws BadCredentialsException if email or password is invalid
     */
    public String login(String email, String rawPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->new BadCredentialsException("Invalid Email or Password"));

        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new BadCredentialsException("Invalid Email or Password");
        }

        // Generate JWT with email + roles
        return jwtUtil.generateToken(user.getEmail(), user.getRoles());
    }

    /**
     * Loads the user from the database by email and builds a Spring Security UserDetails object.
     *
     * @param email the email used as the username.
     * @return a UserDetails object with username, password, and roles.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0])) // This will split comma-separated roles
                .build();
    }
}
