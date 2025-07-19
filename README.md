🏦 **FinGuard – user-service**

The `user-service` is a core microservice in the **FinGuard** platform — a secure, cloud-native fintech application.
This service handles user registration, secure authentication, and profile management using JWT-based stateless security.

---

🚀 Features

- ✅ User Registration with validation
- ✅ Secure Login with JWT token generation
- ✅ Password hashing using BCrypt
- ✅ Role-based access structure
- ✅ MySQL database integration via Spring Data JPA
- ✅ Docker-ready and Kubernetes-deployable
- ✅ Designed using Clean Architecture principles
- 🔐 Secure coding practices aligned with OWASP

---

🧠 System Design Summary

📐 High-Level Architecture


React Frontend → Spring Cloud Gateway → user-service → MySQL

Auth + Notification Services (future)


- **Framework**: Spring Boot
- **Auth**: Spring Security + JWT
- **DB**: MySQL (Dockerized)
- **API**: RESTful (Swagger-enabled)
- **Deployment**: Docker + Kubernetes
- **Monitoring**: Prometheus, Grafana (planned)
- **CI/CD**: Jenkins + GitHub Actions (planned)

📄 [Full System Design](docs/system-design.md) *(coming soon)*

---

## 📦 Project Structure

user-service/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── config/
├── util/
├── application.yml
├── Dockerfile
└── pom.xml


---

## 🛠️ Tech Stack

| Layer         | Tool/Framework        |
|---------------|------------------------|
| Language      | Java 17                |
| Framework     | Spring Boot            |
| Security      | Spring Security + JWT |
| Database      | MySQL + Spring JPA     |
| Container     | Docker                 |
| Orchestration | Kubernetes (Minikube)  |
| CI/CD         | Jenkins, GitHub        |
| API Testing   | Postman                |

---

## 🧪 Running Locally

### Prerequisites
- Java 17+
- Maven
- Docker
- IntelliJ or VS Code

### Steps
```bash
# Clone repo
git clone https://github.com/your-username/finguard-user-service.git
cd finguard-user-service

# Build & Run
mvn clean install
mvn spring-boot:run

```

---


### APIEndpoints(to be added)

| Endpoint        | Method | Description    |
| --------------- | ------ | -------------- |
| `/api/register` | POST   | Register user  |
| `/api/login`    | POST   | Login with JWT |


### Notes 
A. UserRepositroy.java 
    
    1. DAO Layer - Handles all database interactions via repositories. Keeps persistence logic separate from business logic.
    2. JpaRepository<User, Long> - A Spring Data JPA interface giving you built-in CRUD methods for the User entity with Long as the primary key
    3. Optional<User> - A safe way to handle the case when a user is not found, without risking null

B. UserService.java
    
    1. private final UserRepository userRepository;
        -   private: means only this class can directly access this field
        -   final: means the reference cannot be changed after it’s set once.
        -   UserRepository: is the type of the object being referenced.
        -   userRepository: is the name of the variable.
        -   Declaring it final ensures your service always has the required dependency.
    
    2. Spring sees @Autowired and knows it must pass an object of UserRepository when creating UserService.
    
    3. private → encapsulation → hide details from outside classes.
       final → once assigned, can’t point to a new object.
    
    4. What is BCrypt?
        - BCrypt is a password hashing algorithm.
        - Instead of storing plain-text passwords in the database, we:
            -   Hash the password → store only the hash.
            -   When a user logs in, hash their input and compare hashes.
        - BCrypt is strong because it:
            -   Generates different hashes every time (even for the same password).
            -   Adds a random salt to protect against rainbow table attacks.
            -   Is slow by design → harder for hackers to brute force.
        - Example:
            -   Let’s say a user registers with: Password → “mySecret123”
            -   When saved: Hashed → $2a$10$B5m1... (a long scrambled string)
        - Every hash is unique, even for the same password.
        - Verifying Passwords During Login:
            -   boolean match = passwordEncoder.matches(rawPassword, storedHash);
            -   Returns true if passwords match.
        - Benefits of BCrypt :
            -    Salting (protects against rainbow tables)
            -    Slow hashing (makes brute-force hard)
            -    Secure default in modern applications

C. UserController
    
    -   @RestController - Tells Spring this class is a REST API.
        -   Automatically returns JSON (or plain text).
    -   @RequestMapping("/api")
        -   Every endpoint in this class starts with /api.
    -   @PostMapping("/register")
        -   Maps HTTP POST requests to /api/register.
    -   @RequestBody
        -   Converts JSON → Java object (our DTO).
    -   Password hashing:
        -   passwordEncoder.encode(request.getPassword())

D. UserRegistrationRequest - DTO (This class represents the JSON payload the client sends to our endpoint.)
    
    1.  What is DTO ?
        -   DTO = Data Transfer Object.
        -   It maps incoming JSON from the client → Java object.
        -   Instead of exposing your JPA User entity directly in the API,
            it’s best practice to use a DTO (Data Transfer Object).
        -   Keeps your domain model decoupled from API contracts
        -   Prevents exposing sensitive fields

    2. Field Validation:
        -   Bean Validation (e.g. Jakarta Validation / Hibernate Validator) provides these standard annotations:
                    @NotBlank – can’t be empty
                    @Size(min, max) – controls length
                    @Email – validates email format
                    @Pattern(regexp = …) – regex-based checks
        -   We can also validate our password field as 
                    @NotBlank
                    @Size(min = 8, max = 30)
                    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                                message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
                    private String password;

        -    Example Regexes

            | Requirement                | Regex                                                      |
            | -------------------------- | ---------------------------------------------------------- |
            | At least 8 chars, any char | `^.{8,}$`                                                  |
            | At least one uppercase     | `.*[A-Z].*`                                                |
            | At least one lowercase     | `.*[a-z].*`                                                |
            | At least one digit         | `.*\\d.*`                                                  |
            | At least one special char  | `.*[!@#$%^&*()].*`                                         |
            | Combine all                | `^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$` |




E. SecurityConfig 
    
    -   Spring Security blocks all requests by default
    -   We need to allow anonymous users to hit /api/register.
    -   csrf().disable() → disables CSRF protection for APIs.
    -   .requestMatchers("/api/register").permitAll() → allows anyone to call /api/register.


### TroubleShooting Logs:
        -   I was developing a user registration API in Spring Boot for the FinGuard platform.
        -   I needed to ensure the API securely stored user data in MySQL and allowed public registration.
        -   I troubleshot MySQL connection issues, restored missing files after a bad rebase,
            and reconfigured Spring Security to allow unauthenticated requests for registration.
            I debugged 401/403 errors and fixed API requests in Postman.

       1.  Database Connection Errors
            a. Symptoms : App startup failed:
                    -       Access denied for user 'root'@'172.17.0.1'
                            Unknown database 'finguard'

            b. Root Cause: 
                    -       Wrong MySQL password.
                            Database didn’t exist.

            c.  Solution:
                    -       CREATE DATABASE finguard;
                    -       Updated : spring.datasource.url=jdbc:mysql://localhost:3306/finguard

       2.    Spring Security Blocking APIs (401, 403)
            
            -   Initially saw: 
                    -       401 Unauthorized
                            403 Forbidden

            -   Root Cause:
                    -       Security blocked the /api/register endpoint.

            -   Solution :
                    -       http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz
                                    .requestMatchers("/api/register").permitAll()
                                    .anyRequest().authenticated()
                                )
                                .httpBasic(Customizer.withDefaults());

                    -       http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authz -> authz
                                        .anyRequest().permitAll()
                                )
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .formLogin(form -> form.disable());
                        return http.build();


                    -       Registered BCryptPasswordEncoder as a bean.

       3.   HTTP 400 Bad Request
            -   Problem:
                    -       400 Bad Request

            -   Root Cause:
                    -       Postman had trailing %0A in URL.
                            DTOs were incomplete.

            -   Solution:
                    -       Correct Postman request:
                                    POST http://localhost:8081/api/register

                    -       Sent valid JSON:
                                    {
                                      "name": "Shubham",
                                      "email": "shubham@example.com",
                                      "password": "MySecret123"
                                    }

       # Lessons Learned:
            Always check Postman URLs for typos.
            Register all security beans in Spring Boot (e.g. BCryptPasswordEncoder).
            Debugging logs help pinpoint security issues.
            IntelliJ’s Local History can save your day!

### Test you APIs

        1. /api/register - check is data stores in DB
            a.  POST http://localhost:8081/api/register       //Check the port for your application 
            b.  Header : Content-Type : application/json
            c.  JSON body: 
                            {
                                "name": "Shubham",
                                "email": "shubham@example.com",
                                "password": "MySecret123"
                            }

        2.  /api/register - Do field validation
                Send Bad request (missing password , missing email, etc)
            a.  POST http://localhost:8081/api/register
            b.  Bad Request: 
                            {
                                "name": "Shubham",
                                "email": "shubham@example.com",
                                "password": ""
                            }
            c.  Expected response:
                            {
                                "timestamp": "2025-07-15T17:30:10.612",
                                "status": 400,
                                "errors": [
                                    "password : Password is required",
                                    "password : Password must be between 8 and 20 characters"
                                ]
                            }

