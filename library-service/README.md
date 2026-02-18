# Library Service

Library Service handles the core business domain of the Library Platform.

Responsibilities:

- Book catalog management
- Book copy inventory tracking
- Borrow and return workflows
- Loan lifecycle management
- Business rule enforcement (availability, due dates, max active loans)
- JWT-based authorization validation

This service validates access tokens issued by the Auth Service and applies domain-level authorization rules.

Technologies:
- Spring Boot
- Spring Security (JWT validation)
- PostgreSQL
- Flyway
- Docker
