# sample-payments-service

A modern **Spring Boot payments microservice** demonstrating **contract-first API development** with a clean separation between API contracts and application logic.

## Key Features

- **Contract-First Development** – API models & interfaces generated from OpenAPI specification
- **Multi-Module Architecture** – Clear separation between API contracts (`payments-api`) and business logic (`payments-app`)
- **Dual Database Support** – H2 for local development, PostgreSQL for production
- **Lombok Integration** – Reduced boilerplate with auto-generated builders and accessors
- **Java 8 Time API** – Modern date/time handling with `LocalDate` and `LocalDateTime`
- **Exception Handling** – Centralized error handling with custom exceptions
- **MapStruct Mapping** – Type-safe entity-to-DTO conversions

---

## Project Structure

```
sample-payments-service/
├── payments-api/                          # API contract module
│   ├── src/main/resources/spec/
│   │   └── payment-api.yaml              # OpenAPI 3.0 specification
│   ├── target/generated-sources/openapi/
│   │   ├── com/payments/api/             # Generated API interfaces
│   │   └── com/payments/model/           # Generated DTOs with Lombok
│   └── pom.xml
│
├── payments-app/                          # Application logic module
│   ├── src/main/java/com/payments/
│   │   ├── controller/                   # REST controllers implementing generated APIs
│   │   │   └── PaymentController.java
│   │   ├── entity/                       # JPA entities
│   │   │   └── Payment.java
│   │   ├── repository/                   # Spring Data repositories
│   │   │   └── PaymentRepository.java
│   │   ├── service/                      # Business logic
│   │   │   ├── PaymentService.java
│   │   │   └── PaymentServiceImpl.java
│   │   ├── mapper/                       # MapStruct mappers
│   │   │   └── PaymentMapper.java
│   │   ├── exception/                    # Custom exceptions
│   │   │   ├── PaymentNotFoundException.java
│   │   │   └── InvalidPaymentException.java
│   │   ├── advice/                       # Global exception handlers
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── config/                       # Configuration classes
│   │   │   └── OpenApiConfig.java
│   │   └── PaymentsApplication.java      # Main application class
│   ├── src/main/resources/
│   │   ├── application.yml               # Application configuration
│   │   └── application-prod.yml          # Production configuration
│   └── pom.xml
│
├── pom.xml                                # Parent POM
└── README.md
```

---

## Architecture Overview

### Multi-Module Design

#### **payments-api** (Contract Module)
- Contains the OpenAPI specification (`payment-api.yaml`)
- Generates API interfaces and DTOs at build time using `openapi-generator-maven-plugin`
- Acts as a contract between frontend and backend teams
- Can be published as a JAR for client generation

#### **payments-app** (Implementation Module)
- Implements the generated API interfaces
- Contains all business logic, persistence, and configuration
- Depends on `payments-api` module
- Handles database interactions, validation, and error handling

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **API Generation** | OpenAPI Generator 7.2.0 |
| **Framework** | Spring Boot 3.x |
| **Persistence** | Spring Data JPA, Hibernate |
| **Database** | H2 (dev), PostgreSQL (prod) |
| **Mapping** | MapStruct |
| **Boilerplate Reduction** | Lombok |
| **Date/Time** | Java 8 Time API |
| **Build Tool** | Maven 3.8+ |
| **Java Version** | Java 17+ |

---

## Prerequisites

- **JDK 17** or higher
- **Maven 3.8+**
- **PostgreSQL 14+** (for production profile)
- **IDE** with Lombok and MapStruct annotation processor support (IntelliJ IDEA, Eclipse, VS Code)

---

## Build and Run

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/sample-payments-service.git
cd sample-payments-service
```

### 2. Build the Project

```bash
# Clean and build all modules
mvn clean install

# Skip tests during build
mvn clean install -DskipTests
```

This will:
1. Generate API interfaces and models from `payment-api.yaml`
2. Compile both `payments-api` and `payments-app` modules
3. Run tests (if not skipped)

### 3. Run Locally (H2 Database)

```bash
cd payments-app
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar payments-app/target/payments-app-1.0.0.jar
```

**Application will start on:** `http://localhost:8080`

### 4. Access H2 Console

Navigate to: `http://localhost:8080/h2-console`

**Connection Settings:**
- **JDBC URL:** `jdbc:h2:mem:mydb`
- **Username:** `sa`
- **Password:** `sa`

---

## Database Configuration

### Local Development (H2)

```yaml
# application.yml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:mydb
    username: sa
    password: sa
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
    show-sql: true
```

### Production (PostgreSQL)

Create `application-prod.yml`:

```yaml
# application-prod.yml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/payments_db?TimeZone=UTC
    username: postgres
    password: your_password
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          time_zone: UTC
    show-sql: false
```

**Run with production profile:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## APIs

### Base URL
```
http://localhost:8080/payment
```

### Endpoints

#### 1. Create Payment

**POST** `/api/v1/payments`

**Request Body:**
```json
{
  "amount": 100.50,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD",
  "description": "Purchase of product XYZ",
  "customerEmail": "customer@example.com",
  "paymentDate": "2025-12-25"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 100.50,
  "currency": "USD",
  "status": "PENDING",
  "paymentMethod": "CREDIT_CARD",
  "description": "Purchase of product XYZ",
  "customerEmail": "customer@example.com",
  "paymentDate": "2025-12-25",
  "createdAt": "2025-12-25T10:30:00"
}
```

#### 2. Get Payment by ID

**GET** `/api/v1/payments/{id}`

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 100.50,
  "currency": "USD",
  "status": "COMPLETED",
  "paymentMethod": "CREDIT_CARD",
  "description": "Purchase of product XYZ",
  "customerEmail": "customer@example.com",
  "paymentDate": "2025-12-25",
  "createdAt": "2025-12-25T10:30:00",
  "updatedAt": "2025-12-25T10:35:00"
}
```

#### 3. Get All Payments

**GET** `/api/v1/payments`

**Query Parameters:**
- `page` (optional, default: 0)
- `size` (optional, default: 20)
- `status` (optional, filter by status)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "amount": 100.50,
      "currency": "USD",
      "status": "COMPLETED",
      "customerEmail": "customer@example.com"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

#### 4. Update Payment Status

**PATCH** `/api/v1/payments/{id}/status`

**Request Body:**
```json
{
  "status": "COMPLETED"
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "updatedAt": "2025-12-25T10:35:00"
}
```

#### 5. Delete Payment

**DELETE** `/api/v1/payments/{id}`

**Response (204 No Content)**

---

## Testing

### Run All Tests

```bash
mvn test
```

### Run Tests for Specific Module

```bash
# Test only the app module
mvn test -pl payments-app

# Test with coverage
mvn clean test jacoco:report
```



## 🛠️ Development Workflow

### 1. Modify API Contract

Edit `payments-api/src/main/resources/spec/payment-api.yaml`

### 2. Regenerate API Code

```bash
cd payments-api
mvn clean compile
```
---

## API Documentation

### Swagger UI

Access interactive API documentation at:

```
http://localhost:8080/payment/swagger-ui.html
```

### OpenAPI JSON

Raw OpenAPI specification available at:

```
http://localhost:8080/payment/v3/api-docs
```

---


## Deployment

### Docker

Create `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY payments-app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
docker build -t sample-payments-service .
docker run -p 8080:8080 sample-payments-service
```



---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Authors

- **Ankit Singh Rawat** - *Initial work* - [YourGitHub](https://github.com/yourusername)

---

## Acknowledgments

- Spring Boot team for the amazing framework
- OpenAPI Initiative for API specifications
- Lombok and MapStruct projects for productivity tools