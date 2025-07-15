package com.finguard.userservice.controller;


import com.finguard.userservice.dto.UserRegistrationRequest;
import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Automatically returns JSON (or plain text)
@RestController     //Tells Spring this class is a REST API.
@RequestMapping("/api")     //Every endpoint in this class starts with /api
public class UserController {

   // @Autowired // instead of final can use this for field injection
    // private  UserRepository userRepository;

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired // this is construction injection
    public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // @RequestBody - Converts JSON → Java object (our DTO)
    // @PostMapping - Maps HTTP POST requests to /api/register.
    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegistrationRequest request){
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
