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
#1. Clone repo
git clone https://github.com/your-username/finguard-user-service.git
cd finguard-user-service

# Build & Run
mvn clean install
mvn spring-boot:run

```
##2. MySQL Configuration
    
    -   Ensure your database is set up correctly:
        CREATE DATABASE finguard;
    -   Update your application.yml:
            spring:
                datasource:
                    url: jdbc:mysql://localhost:3306/finguard
                    username: root
                    password: yourpassword


##3. Register a User (/api/register)

    -   Endpoint: POST http://localhost:8081/api/register
    -   Headers: Content-Type: application/json
    -   JSON Body:
            {
                "name": "Shubham",
                "email": "shubham@example.com",
                "password": "MySecret123"
            }
    -   Data will be securely hashed using BCrypt and stored in MySQL.

##4.  Login and Get JWT Token (/api/login)

    -   Endpoint: POST http://localhost:8081/api/login
    -   Headers: Content-Type: application/json
    -   JSON Body:
            {
                "email": "shubham@example.com",
                "password": "MySecret123"
            }
    -    If credentials are correct, you'll receive:
            {
                 "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            }
    -   If incorrect:
            {
                "error": "Invalid email or password"
            }



| Component                            | Purpose                                                                                                                              |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------ |
| `SecurityConfig`                     | Configures Spring Security: disables sessions, enables stateless JWT auth, allows `/api/login` and `/api/register`, protects others. |
| `JwtAuthenticationFilter`            | Intercepts every request, extracts the JWT from header, validates it, and sets `SecurityContext`.                                    |
| `JwtUtil`                            | Creates and validates JWT tokens.                                                                                                    |
| `UserService` / `UserDetailsService` | Loads user info from DB for login flow.                                                                                              |
| `BCryptPasswordEncoder`              | Secure password hashing.                                                                                                             |

---


### APIEndpoints(to be added)

| Endpoint         | Method | Description                          | Headers Required                  | Auth Required |
|------------------|--------|--------------------------------------|-----------------------------------|---------------|
| `/api/register`  | POST   | Register a new user                  | `Content-Type: application/json`  | ❌ No         |
| `/api/login`     | POST   | Login with credentials, returns JWT  | `Content-Type: application/json`  | ❌ No         |
| `/api/secure`    | GET    | Test protected endpoint with JWT     | `Authorization: Bearer <token>`   | ✅ Yes        |
| `/api/profile`   | GET    | Fetch user profile (if implemented)  | `Authorization: Bearer <token>`   | ✅ Yes        |



### How to Decode & Verify JWT Tokens

    -   Once you receive a JWT from the /api/login endpoint, you can manually inspect and validate the token using the following methods:

    -   Online Tool: jwt.io
        -   Go to https://jwt.io
        -   Paste the token into the left panel.
        -   The right panel will show:
        -   sub (email)
        -   roles
        -   iat (issued at)
        -   exp (expiration)
        -   ⚠️ Don’t paste sensitive tokens in production — use this only for testing.

    -   Terminal Method (Linux/macOS)
        -   If you just want to view the payload:
            -   echo 'your.jwt.token.here' | cut -d '.' -f2 | base64 -d
        -   If the JWT is Base64Url encoded (often the case), use:
            -   echo 'your.jwt.token.here' | cut -d '.' -f2 | tr '_-' '/+' | base64 -d
        -   If it give error try padding mannualy  (JWT payloads are often missing =):
            -   PAYLOAD=$(echo 'your.jwt.token.here')
                PADDED=$(printf "%s" "$PAYLOAD" | awk '{ while (length % 4 != 0) $0=$0"="; print }')
                echo "$PADDED" | tr '_-' '/+' | base64 --decode
        


    -   What to Check in a JWT?
            | Field   | Meaning                          | Example            |
            | `sub`   | Subject — usually the user email | `user@example.com` |
            | `roles` | User roles                       | `[ "USER" ]`       |
            | `iat`   | Issued At (Unix Timestamp)       | `1720000000`       |
            | `exp`   | Expiration (Unix Timestamp)      | `1720086400`       |

        -   To convert timestamps into human-readable format:
            -   date -d @1720086400





### Notes - Some Key Concepts 
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

F. JwtUtil

    -   We added a secure login mechanism using JWT (JSON Web Tokens) to authenticate users in a stateless, scalable way.
    
    -   What is JwtUtil?
            -   JwtUtil is a utility class that performs operations related to JWTs. In our project, it's used to:
                Generate a token when a user successfully logs in. 
                Validate an incoming token to make sure it hasn’t been tampered with or expired.
                Extract claims (like email/username) from the token.
    -   What a Basic JwtUtil Class Does :
                    package com.finguard.userservice.util;
        
                    import io.jsonwebtoken.*;
                    import io.jsonwebtoken.security.Keys;
                    import org.springframework.stereotype.Component;
                    
                    import java.security.Key;
                    import java.util.Date;
                    
                    @Component
                    public class JwtUtil {
                    
                        private final long expiration = 1000 * 60 * 60 * 24; // 24 hours
                    
                        // SECRET KEY (used for signing and validating token)
                        private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                    
                        // 1. Generate JWT
                        public String generateToken(String email) {
                            return Jwts.builder()
                                    .setSubject(email)                     // what user (email) this token is for
                                    .setIssuedAt(new Date())               // when it was created
                                    .setExpiration(new Date(System.currentTimeMillis() + expiration)) // expiry
                                    .signWith(key)                         // signature using secret key
                                    .compact();                            // convert to token string
                        }
                    
                        // 2. Extract Email
                        public String extractEmail(String token) {
                            return parseToken(token).getBody().getSubject();
                        }
                    
                        // 3. Validate Token
                        public boolean isTokenValid(String token, String email) {
                            String extractedEmail = extractEmail(token);
                            return extractedEmail.equals(email) && !isTokenExpired(token);
                        }
                    
                        // Check Expiry
                        private boolean isTokenExpired(String token) {
                            return parseToken(token).getBody().getExpiration().before(new Date());
                        }
                    
                        // Parse and validate signature
                        private Jws<Claims> parseToken(String token) {
                            return Jwts.parserBuilder()
                                    .setSigningKey(key)   // validate signature
                                    .build()
                                    .parseClaimsJws(token);
                        }
                    }
            - Breakdown of Key Concepts
                    1. @Component: Registers this class as a Spring Bean, so it can be @Autowired wherever needed.
                    
                    2. Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
                        This creates a random secret key used to sign the token.
                        In production, you'll fetch this securely (e.g., from AWS Secrets Manager).
                        HS256 is a common and secure HMAC algorithm.
                    
                    3. generateToken(String email)
                        Takes in the user’s email and returns a signed JWT string.
                        setSubject(email) stores email in token.
                        setExpiration(...) sets how long token is valid (e.g., 24 hours).
                        signWith(key) signs the token.
                    
                    4. extractEmail(String token)
                       Returns the email (subject) from the token's payload.
                    
                    5. isTokenValid(token, email)
                          Confirms the token: 
                            Belongs to the same email.
                            Hasn’t expired.
                    
                    6. parseClaimsJws(...)
                       Parses the JWT string, validates the signature, and returns claims like subject, expiry, etc.
                    
                    🧪 Example JWT Format
                    JWTs consist of 3 parts:
                        EX:
                        eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsImlhdCI6MTY5NzU5ODkwMCwiZXhwIjoxNjk3Njg1MzAwfQ.jN7...sign

                        Header: Algorithm & token type
                        Payload: Claims like subject (email), issued date, expiry
                        Signature: Ensures token hasn’t been tampered with
                    
                    🛡 Why Use JWTs in Microservices?
                        Stateless: Server doesn’t store sessions.
                        Secure: Cannot be modified without invalidating the signature.
                        Scalable: Easy to validate with just the secret key.

G. JWT Utility with Role-Based Claims
    
    -   To secure the authentication mechanism in our user-service, we added a custom JwtUtil class to generate and validate JWT tokens. 
        These tokens are used to maintain a stateless session and carry essential user information like email and roles.

    -    1.Features Implemented
            -   JWT generation upon successful login.
            -   JWT includes:
                -   sub (email/username)
                -   roles (custom claim with role list)
            -   Methods to extract email and roles from token.
            -   Token validation:
                -   Verifies signature and expiry
                -   Ensures token subject matches logged-in user
    -    2.JWT Example Payload (Decoded)
            {
                "sub": "shubham@example.com",
                "roles": ["USER"],
                "iat": 1721662800,
                "exp": 1721749200
            }

    -   3.How It Works
        -   When the user logs in via /api/login:
            -   Credentials are validated against the DB.
            -   JWT is generated using JwtUtil.generateToken(email, roles). 
            -   Token is returned to the client in the response body.
        -   The client sends this JWT in the Authorization header for future requests:
            -   Authorization: Bearer <token>

        -    If credentials are wrong:
            -   API responds with:
                    {
                            "error": "Invalid Email or Password"
                    }
            -   Status Code: 401 Unauthorized

F. DTOs

    | DTO Class                 | Purpose                         | Validations Present?                                           |             
    | ------------------------- | ------------------------------- | -------------------------------------------------------------- |  
    | `LoginRequest`            | Input payload for login         | Yes (`@NotBlank`)                                              |   
    | `LoginResponse`           | Response payload containing JWT | No validations needed                                          |  
    | `UserRegistrationRequest` | Input payload for registration  | Full validation (`@NotBlank`, `@Email`, `@Size`, `@Pattern`)   | 


G. How to run docker-compose.yaml and test it 

    1. docker-compose.yaml
    2. docker ps
    3. docker exec -it finguard-mysql mysql -u finguard_user -p
       pass : finguard_pass 


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
        A. /api/register 
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

        B. /api/login
                    1. Test /api/login with Postman
                        a. Endpoint: POST http://localhost:8081/api/login
                        b. Header : Content-Type : application/json
                        c. JSON Body
                            {
                                "email": "shubham@example.com",
                                "password": "MySecret123"
                            }

                    2. Expected Response (Success):
                        If the credentials are valid, you should get:
                            {
                                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            }
                         Status Code: 200 OK
                        This token will be used in the Authorization header for future requests.

                    3. Expected Response (Failure):
                        If you enter wrong email or password, you’ll get:
                            {
                                "error": "Invalid email or password"
                            }
                        Status Code: 401 Unauthorized
    
                    4. I encountred 500 Internal Server Error 
                            {
                                "timestamp": "2025-07-19T18:30:55.370+00:00",
                                "status": 500,
                                "error": "Internal Server Error",
                                "path": "/api/login"
                            }
                        Problem: 500 Internal Server Error when hitting /api/login, which means something failed inside my backend code 
                                    — likely during authentication or token generation.
                        Root Cause : Cannot invoke "com.finguard.userservice.service.UserService.login(String, String)" because "this.userService" is null
                                     Spring Boot did not inject the UserService dependency into your UserController — so userService is null at runtime.
                                     I forgot to include UserService in my UserController constructor during constructor injection.
        
        C. /api/secure — Protected Route
                    
                    1. Once User is registerd and token is generated from login api 
                    2. Test /api/secure
                        a. GET http://localhost:8081/api/secure
                        b. Authorization: Bearer <your-token-from-login>
                    3. Expected Result: 
                        a. if token is valid 
                            200 ok 
                            (proctected content here)
                        b. if token is missing or invalid
                            403 Forbidden or 401 Unauthorized


                        

### Docker Setup

1.  MySQL Docker Integration (For Local Development)
    | Item               | Value               |
    | ------------------ | ------------------- |
    | **Container Name** | `mysql`             |
    | **Image Used**     | `mysql:8.0`         |
    | **Port Exposed**   | `3306:3306`         |
    | **Database Name**  | `finguard`          |
    | **Username**       | `root`              |
    | **Password**       | (set via container) |

    # Setup Instructions
        a. Make sure the container is running: docker ps
        b. You should see the MySQL container:
                CONTAINER ID   IMAGE       PORTS           NAMES
                8eba629cafce   mysql:8.0   3306->3306/tcp  mysql
        c. To connect inside:
                docker exec  -it mysql mysql -u root -p 
                # Enter your password 
    # DB & Table Validation:
        Inside my sql terminal:
            SHOW DATABASES;
            USE finguard;
            SHOW TABLES;
            SELECT * FROM users;



### Unit Testing 
            NEVER mock the class you're testing. You only mock its dependencies.

1 .UserServiceTest.java
         
    This section includes thorough unit tests for the service layer using JUnit 5 and Mockito to ensure the integrity and reliability of core business logic.
        
    a. **UserService Tests**: Cover scenarios such as:
            Valid user registration with password hashing verification.
            Duplicate email registration rejection.
            Edge case validation for null or missing inputs.
            Successful login and authentication failure cases.

    b. **Testing Tools and Annotations**:
            @ExtendWith(MockitoExtension.class) to enable Mockito integration with JUnit 5.
            @Mock annotations to mock dependencies like UserRepository, BCryptPasswordEncoder, and JwtUtil.
            @InjectMocks to inject mocked dependencies into the tested UserService instance.
            Use of verify() to assert method invocation on mocks.

2. JwtUtilTest.java 

        The JwtUtilTest class verifies the correctness of the JwtUtil component used to generate, parse, and validate 
        JSON Web Tokens (JWT) in the FinGuard backend. These tokens enable stateless authentication across microservices.
    
        
        | Test Method                                    | Description                                                              |
        | ---------------------------------------------- | ------------------------------------------------------------------------ |
        | `testGenerateToken_containsCorrectEmail()`     | Ensures generated token contains the correct email as subject            |
        | `testGenerateToken_containRoles()`             | Verifies that user roles are correctly embedded and extracted from token |
        | `testIsTokenValid_validToken_returnsTrue()`    | Confirms that a valid token is accepted when matching email is given     |
        | `testExtractEmail_success()`                   | Ensures correct extraction of subject (email) from token                 |
        | `testExtractRoles_success()`                   | Ensures correct extraction of roles from token                           |
        | `testIsTokenValid_invalidEmail_returnsFalse()` | Checks that validation fails when token email does not match input       |
        | `testIsTokenExpired_falseForValidToken()`      | Verifies that non-expired token is not mistakenly flagged as expired     |

        Note:   No mocking required: JwtUtil is a self-contained utility class without external dependencies.
                Real object tested: Uses actual methods to verify behavior (e.g., generate → extract → validate).
                We're testing JwtUtil itself (a utility class)
                    -   It has no external dependencies like a repository or service.
                    -   All methods are pure, self-contained logic — no DB, no network, no Spring beans.

        Sample Token Flow Tested :
                User Email + Roles  →  JwtUtil.generateToken()
                                            ↓
                                    JWT Token String
                                            ↓
                                    JwtUtil.extractEmail(token)
                                    JwtUtil.extractRoles(token)
                                    JwtUtil.isTokenValid(token, email)



