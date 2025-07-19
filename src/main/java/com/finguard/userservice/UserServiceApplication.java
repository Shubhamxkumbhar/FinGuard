package com.finguard.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Main entry point for the FinGuard User Service Spring Boot application.
 * Starts the embedded server and initializes the application context.
 * {code@SpringBootApplication}: This is a meta-annotation that includes
 *      @Configuration: Marks this class as a source of bean definitions (for dependency injection).
 *      @EnableAutoConfiguration: Tells Spring Boot to auto-configure beans based on classpath and properties.
 *      @ComponentScan: Tells Spring to scan this package and sub-packages for components, services, etc.
 * Why use it? It reduces boilerplate and helps Spring Boot start up with sensible defaults.
 */
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String args[]){
        SpringApplication.run(UserServiceApplication.class, args);
        /* Method: SpringApplication.run()
            This method:
            Boots up the embedded server (like Tomcat)
            Creates the Spring application context
            Scans the project for Spring components
            Starts the app
            ➡Think of it as the main() function for your Spring Boot microservice.
         */
    }
}
