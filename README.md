# Task Management Application

A full-stack task management application built with Spring Boot and Angular.

## Features

- User authentication with JWT
- Task creation, assignment, and management
- Role-based access control
- RESTful API design

## Technology Stack

- **Backend**: Spring Boot, Spring Security, Spring Data JPA, PostgreSQL
- **Frontend**: Angular
- **Infrastructure**: Docker, Docker Compose

## Getting Started

### Prerequisites

- Docker and Docker Compose
- JDK 17+ (for local development without Docker)
- Node.js and npm (for local frontend development without Docker)

### Environment Setup

1. Clone this repository

2. Create an environment file `.env`

3. Edit the `.env` file to set your environment-specific values

```
JWT_SECRET=your_secure_random_string
POSTGRES_PASSWORD=your_secure_database_password
```

### Running with Docker Compose

Start all services:

```bash
docker-compose up
```

For development mode with hot reload:

```bash
docker-compose -f docker-compose.dev.yml up
```

## API Documentation

API documentation is available at [API-DOCUMENTATION.md](spring-app/API-DOCUMENTATION.md)

## License

[MIT License](LICENSE)
