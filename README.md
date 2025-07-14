🏦 **FinGuard – user-service**

The `user-service` is a core microservice in the **FinGuard** platform — a secure, cloud-native fintech application. This service handles user registration, secure authentication, and profile management using JWT-based stateless security.

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
        -    




