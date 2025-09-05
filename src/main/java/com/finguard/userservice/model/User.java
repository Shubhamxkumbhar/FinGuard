package com.finguard.userservice.model;

import jakarta.persistence.*;   /* Jakarta Persistence (jakarta.persistence) is the successor to JPA (Java Persistence API).
 It's used to map Java objects to relational database tables.
    Spring Boot uses Hibernate (an ORM library) under the hood to handle these mappings automatically.
*/
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a user in the database.
 * <p>
 * Maps to the users table and stores user details
 * including email, password, name, and roles.
 * </p>
 */
@Entity                 //Marks this class as a JPA entity (maps to DB table)
@Table(name="users")    // Optional – explicitly names the table
@Getter
@Setter
@NoArgsConstructor      // Automatically generate a no-argument constructor
@AllArgsConstructor     //  Automatically generate an all-argument constructor
public class User {     // This Class defines the structure of the users table

    /**
     * Primary key of the user entity.
     */
    @Id                 // Marks the id as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Lets the DB auto-generate the ID
    private long id;

    /**
     * User's full name.
     */
    private String name;

    /**
     * User's email address (must be unique).
     */
    @Column(unique = true, nullable = false)    // Email must be unique and not null
    private String email;

    /**
     * Hashed user password.
     */
    private String password;

    /**
     * User roles (e.g., USER, ADMIN).
     * code{ElementCollection}  - tells JPA to create a separate table for this list.
     * fetch = FetchType.EAGER ensures roles are loaded with the user object (important for auth).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;


    private LocalDateTime createdAt = LocalDateTime.now();

}
