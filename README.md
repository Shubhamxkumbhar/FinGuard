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



