package com.finguard.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration      // Marks this as a class that provides Spring Beans.
public class SecurityConfig {

    // BCryptPasswordEncoder - Provides methods to hash and check passwords
    @Bean           // Registers a method’s return object in the Spring container
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
