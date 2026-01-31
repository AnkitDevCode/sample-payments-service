# Sample-payments-service

A modern **Spring Boot payments microservice** demonstrating **contract-first API development** with a clean separation
between API contracts and application logic.

## Key Features

- **Contract-First Development** – API models & interfaces generated from OpenAPI specification
- **Multi-Module Architecture** – Clear separation between API contracts (`payments-api`) and business logic (
  `payments-app`)
- **Dual Database Support** – H2 for local development, PostgreSQL for production
- **Lombok Integration** – Reduced boilerplate with auto-generated builders and accessors
- **Java 8 Time API** – Modern date/time handling with `LocalDate` and `LocalDateTime`
- **Exception Handling** – Centralized error handling with custom exceptions

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

| Layer                     | Technology                  |
|---------------------------|-----------------------------|
| **API Generation**        | OpenAPI Generator 7.2.0     |
| **Framework**             | Spring Boot 3.x             |
| **Persistence**           | Spring Data JPA, Hibernate  |
| **Database**              | H2 (dev), PostgreSQL (prod) |
| **Mapping**               | MapStruct                   |
| **Boilerplate Reduction** | Lombok                      |
| **Date/Time**             | Java 8 Time API             |
| **Build Tool**            | Maven 3.8+                  |
| **Java Version**          | Java 17+                    |

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
git clone https://github.com/AnkitDevCode/sample-payments-service.git
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

Navigate to: `http://localhost:8080/api/v1/h2-console`

**Connection Settings:**

- **JDBC URL:** `jdbc:h2:mem:mydb`
- **Username:** `sa`
- **Password:** `sa`

---

## APIs

# Payments API

This service provides REST APIs to **create and retrieve payments** with explicit
**debtor** and **creditor** information.

Base URL:

```
http://localhost:8080/
```

---

## Create a Payment

### Endpoint

```
POST /api/v1/payments
```

### Sample Request

```json
{
  "amount": 999.99,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD",
  "debtor": {
    "name": "John Doe",
    "accountNumber": "1234567890",
    "bankCode": "HDFC0001234",
    "address": "123 Main Street, New York, NY 10001",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890"
  },
  "creditor": {
    "name": "ABC Store",
    "accountNumber": "9876543210",
    "bankCode": "ICIC0005678",
    "address": "456 Commerce Ave, Los Angeles, CA 90001",
    "email": "payments@abcstore.com",
    "phoneNumber": "+1987654321"
  }
}
```

### Sample Response

```json
{
  "paymentId": "6823c05d-dd9d-4bab-a2d4-af9c22bc5fb0",
  "amount": 999.99,
  "currency": "USD",
  "status": "PENDING",
  "paymentMethod": "CREDIT_CARD",
  "debtor": {
    "name": "John Doe",
    "accountNumber": "1234567890",
    "address": "123 Main Street, New York, NY 10001",
    "bankCode": "HDFC0001234",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890"
  },
  "creditor": {
    "name": "ABC Store",
    "accountNumber": "9876543210",
    "address": "456 Commerce Ave, Los Angeles, CA 90001",
    "bankCode": "ICIC0005678",
    "email": "payments@abcstore.com",
    "phoneNumber": "+1987654321"
  },
  "createdAt": "2026-01-04T11:22:18.046674",
  "links": {
    "self": {
      "href": "http://localhost:8080/api/v1/payments/6823c05d-dd9d-4bab-a2d4-af9c22bc5fb0",
      "rel": "self"
    }
  },
  "updatedAt": "2026-01-04T11:22:18.046674"
}
```

---

## Get Payment by ID

### Endpoint

```
GET /api/v1/payments/{paymentId}
```

### Sample Response

```json
{
  "paymentId": "6823c05d-dd9d-4bab-a2d4-af9c22bc5fb0",
  "amount": 999.99,
  "currency": "USD",
  "status": "PENDING",
  "paymentMethod": "CREDIT_CARD",
  "debtor": {
    "name": "John Doe",
    "accountNumber": "1234567890",
    "address": "123 Main Street, New York, NY 10001",
    "bankCode": "HDFC0001234",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890"
  },
  "creditor": {
    "name": "ABC Store",
    "accountNumber": "9876543210",
    "address": "456 Commerce Ave, Los Angeles, CA 90001",
    "bankCode": "ICIC0005678",
    "email": "payments@abcstore.com",
    "phoneNumber": "+1987654321"
  },
  "createdAt": "2026-01-04T11:22:18.046674",
  "links": {
    "self": {
      "href": "http://localhost:8080/api/v1/payments/6823c05d-dd9d-4bab-a2d4-af9c22bc5fb0",
      "rel": "self"
    }
  },
  "updatedAt": "2026-01-04T11:22:18.046674"
}
```

---

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
http://localhost:8080/api/v1/swagger-ui.html
```

### OpenAPI JSON

Raw OpenAPI specification available at:

```
http://localhost:8080/api/v1/api-docs
```

---

## Deployment

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

- **Ankit Singh Rawat** - [AnkitDevCode](https://github.com/AnkitDevCode)

---

## Acknowledgments
