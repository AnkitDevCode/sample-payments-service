# sample-payments-service

A small Spring Boot service demonstrating a Payment API with a separation between JPA entity and API model, a mapper,
service layer, and controllers. Uses PostgreSQL as the runtime database.

## Table of Contents

- Project structure
- Prerequisites
- Build and run
- API
    - `POST /api/payments`
    - `GET /api/payments/{id}`

## Project structure

- `model/`: Contains the API model classes.
- `entity/`: Contains the JPA entity classes.
- `mapper/`: Contains the mapper classes to convert between entity and model.
- `service/`: Contains the service layer classes.
- `controller/`: Contains the REST controller classes.
- `repository/`: Contains the JPA repository interfaces.
- `exception/`: Contains custom exception classes.
- `advice/`: Contains exception handler classes.
- `config/`: Contains configuration classes for the application.
- `application.properties`: Configuration file for database connection and other settings.
- `pom.xml`: Maven configuration file with dependencies.
- `docker-compose.yml`: Docker Compose file for setting up PostgreSQL database.


## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL database
- Docker (optional, for running PostgreSQL in a container)
- cURL or Postman (for testing the API)
- Spring Boot 3.1 or higher
- Hibernate 6 or higher

## Build and run

1. Clone the repository:
   ```bash
    git clone git@github.com:AnkitDevCode/sample-payments-service.git                       
   ```
2. Navigate to the project directory:
   ```bash
    cd sample-payments-service
    ```
3. Build the project using Maven:
    ```bash
    mvn clean install
    ```
4. Run the application:
    ```bash
    mvn spring-boot:run
    ```

Alternatively, you can use Docker Compose to run the PostgreSQL database and the application:
1. Ensure Docker is installed and running on your machine.
2. Navigate to the project directory.
3. Build the Docker image: 
   ```bash
   docker build -t sample-payments-service:latest .
   ``` 
4. Start the services using Docker Compose: please see the docker/docker-compose.yml file for configuration.

```bash
docker compose up -d
```
5. Access the application logs to verify it is running:

```bash
docker compose logs -f
```
6. Stop the services when done:

```bash
docker compose down
```


5. The application will start on `http://localhost:8080`.

## API

### `POST /api/payments`

Creates a new payment.

- **Request Body**:
  ```json
    {
    "amount": 125.50,
    "currency": "USD",
    "paymentMethod": "NET_BANKING"
    }
  ```
- **Response**:
- **Status**: `201 Created`
- **Body**:
  ```json
    {
    "id": 1,
    "amount": 125.50,
    "currency": "USD",
    "status": "PENDING",
    "paymentMethod": "NET_BANKING",
    "createdAt": "2025-10-25T09:09:54.6955289",
    "updatedAt": "2025-10-25T09:09:54.6955289"
    }
  ```

### `GET /api/payments/{id}`

Retrieves a payment by its ID.

- **Path Parameter**:
    - `id`: The ID of the payment to retrieve.
  - **Response**:
    - **Status**: `200 OK`
        - **Body**:
    ```json
     {
      id": 1,
      "amount": 125.50,
      "currency": "USD",
      "status": "PENDING",
      "paymentMethod": "NET_BANKING",
      "createdAt": "2025-10-25T09:09:54.695529",
      "updatedAt": "2025-10-25T09:09:54.695529"
      }
   ```
- **Status**: `404 Not Found` (if payment does not exist)
- **Body**:
  ```json
   {
    "timestamp": "2025-10-24T17:32:52.0017723",
    "status": 404,
    "error": "Not Found",
    "message": "Payment not found with id: 44",
    "path": "/api/payments/44"
  }
```
