# Task Management API with OAuth 2

This is a REST API for task management using Spring Boot 3.3, Java 21, and OAuth 2 with JWT authentication.

## Features

- User authentication using OAuth 2 and JWT
- Role-based access control (ADMIN, USER)
- Task management (create, read, update, delete)
- RESTful API endpoints
- Exception handling and logging

## Prerequisites

- Java 21
- Maven 3.8+
- Docker (for containerization)
- Kubernetes/EKS (for deployment)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/task-management.git
cd task-management
```

### 2. Build the application

```bash
mvn clean install
```

### 3. Run the application

```bash
java -jar target/task-management-api-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will be available at http://localhost:8080

## API Endpoints

### Authentication

- **POST /auth/signup** - Register a new user
  ```json
  {
    "username": "admin",
    "password": "admin123",
    "role": "ADMIN"
  }
  ```

- **POST /auth/login** - Login and get JWT token
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```
  Response:
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer"
  }
  ```

### Task Management

For all task endpoints, include the JWT token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

- **POST /tasks** - Create a new task
  ```json
  {
    "title": "Implement API",
    "description": "Create a task management API using Spring Boot",
    "status": "PENDING"
  }
  ```

- **GET /tasks** - Get all tasks (ADMIN sees all tasks, USER sees only their own)

- **PUT /tasks/{id}** - Update a task
  ```json
  {
    "title": "Updated task title",
    "description": "Updated description",
    "status": "COMPLETED"
  }
  ```

- **DELETE /tasks/{id}** - Delete a task

Swagger API endpoints can be found at http://localhost:8080/swagger-ui/index.html
