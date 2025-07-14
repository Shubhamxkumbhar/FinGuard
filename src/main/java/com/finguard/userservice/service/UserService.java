package com.finguard.userservice.service;


import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // Marks this as a service layer bean for business logic. Spring will auto-discover it
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;  // Provides methods to hash and check passwords.

    // Constructor Injection (recommended over @Autowired field injection)
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Register a new User
    public User registerUser(User user){
        //Hash the Password
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        return userRepository.save(user);
    }

    //Retrieve a user by email
    public Optional<User> findByEmail(String email){

        return userRepository.findByEmail(email);
    }

    //check if email already taken

    public boolean emailExists(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
