# Backend

Java 21 and Spring Boot backend for the Employee Document Matcher.

## Prerequisites

- JDK 21
- Maven 3.9+
- Docker Desktop

## Database

Start PostgreSQL from the repository root:

```bash
docker compose up -d postgres
```

The defaults are development-only values. Copy `.env.example` to `.env` to
override them locally. Flyway applies migrations when the backend starts.

## Run

```bash
mvn spring-boot:run
```

The health endpoint is available at:

```text
GET http://localhost:8080/actuator/health
```

## Test

```bash
mvn verify
```

Integration tests use a disposable PostgreSQL Testcontainer and therefore require
a running Docker engine.
