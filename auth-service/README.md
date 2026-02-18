# Auth Service

Auth Service is responsible for authentication and token management within the Library Platform ecosystem.

Responsibilities:

- User registration & login
- Password hashing and credential validation
- Access token (JWT) issuance
- Refresh token lifecycle management
- Role-based claims generation
- Secure token validation configuration for other services

This service operates independently and issues JWTs that can be validated offline by other services in the system.

Technologies:
- Spring Boot
- Spring Security
- PostgreSQL
- Flyway
- Docker
