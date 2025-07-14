package com.finguard.userservice.reporsitory;

import com.finguard.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// This interface acts as the DAO layer for the User entity
// DAO - DAO stands for Data Access Object.
// It's a design pattern used to abstract and encapsulate all interactions with a database.
// Acts as a middleman between the Service Layer and the Database. Isolate database-related code from the business logic.
// JpaRepository<User, Long> - this gives us CRUD for free.
// By extending it, Spring automatically provides many useful DB operations – without writing any SQL or methods!
// User → is the Entity class (mapped to a table named users).
//Long → is the type of the primary key (id in User class).
// findAll(); findById(Long id); save(User user); deleteById(Long id)
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA will automatically implement this method to fetch user by email
    //Spring will auto-implement this to generate the SQL query - SELECT * FROM users WHERE email = ?
    // Optional<T> is a Java 8 container class used to avoid NullPointerException.
    // If no user is found by email, it returns Optional.empty() instead of null
    Optional<User> findByEmail(String email);


    // Optional: you can define more methods like existsByEmail(), deleteByEmail(), etc.
}

