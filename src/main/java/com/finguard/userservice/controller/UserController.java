    package com.finguard.userservice.controller;

    import com.finguard.userservice.dto.LoginRequest;
    import com.finguard.userservice.dto.UserRegistrationRequest;
    import com.finguard.userservice.model.User;
    import com.finguard.userservice.reporsitory.UserRepository;
    import com.finguard.userservice.service.UserService;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.web.bind.annotation.*;

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

       // @Autowired // instead of final can use this for field injection
        // private  UserRepository userRepository;

        /**
         * The repository for performing CRUD operations on User entities.
         */
        private final UserRepository userRepository;

        /**
         * Encoder for hashing user passwords securely.
         */
        private final BCryptPasswordEncoder passwordEncoder;

        private final UserService userService;

        /**
         * Constructor injection for UserRepository and BCryptPasswordEncoder.
         *
         * @param userRepository    Repository for User entities.
         * @param passwordEncoder   Encoder for secure password hashing.
         */
        @Autowired // this is construction injection
        public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserService userService) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
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
        public String registerUser(@Valid @RequestBody UserRegistrationRequest request){
            // check if email already exists
            if(userRepository.findByEmail(request.getEmail()).isPresent()){
                return "Email already registered.";
            }

            User user = new User();
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            // Use provided roles if present, else default to "USER"
            if (request.getRoles() == null || request.getRoles().isEmpty()) {
                user.setRoles(Collections.singletonList("USER"));
            } else {
                user.setRoles(request.getRoles());
            }
            userRepository.save(user);

            return "User Registered Successfully!";

        }

        /**
         * Authenticates a user and returns a JWT token if successful.

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){

                LoginResponse response = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
                return ResponseEntity.ok(response);

        } */

        /**
         * Authenticates a user and returns a JWT token if successful.
        */
         @PostMapping("/login")
         public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest){

         try{
             String token = userService.login(loginRequest.email, loginRequest.getPassword());
             return ResponseEntity.ok(Collections.singletonMap("token", token));
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
