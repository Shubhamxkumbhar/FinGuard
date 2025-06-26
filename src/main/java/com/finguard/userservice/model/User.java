package com.finguard.userservice.model;

import jakarta.persistence.*;   /* Jakarta Persistence (jakarta.persistence) is the successor to JPA (Java Persistence API). It's used to map Java objects to relational database tables.
    Spring Boot uses Hibernate (an ORM library) under the hood to handle these mappings automatically.
*/
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity                 //Marks this class as a JPA entity (maps to DB table)
@Table(name="users")    // Optional – explicitly names the table
@Getter
@Setter
@NoArgsConstructor      // Automatically generate a no-argument constructor
@AllArgsConstructor     //  Automatically generate an all-argument constructor
public class User {     // This Class defines the structure of the users table

    @Id                 // Marks the id as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Lets the DB auto-generate the ID
    private long id;

    private String name;

    @Column(unique = true, nullable = false)    // Email must be unique and not null
    private String email;

    private String password;

    private String roles;

    private LocalDateTime createdAt = LocalDateTime.now();


    // Getter and Setter

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }


    // since you've enabled Lombok, we don't need the manually written getters and setters
 /*   public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String setPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getRoles(){
        return roles;
    }

    public void setRoles(String roles){
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
    */
}
