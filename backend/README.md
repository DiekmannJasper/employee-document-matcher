# Backend

Java 21 and Spring Boot backend for the Employee Document Matcher.

## Prerequisites

- JDK 21
- Maven 3.9+

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

Database configuration is added in GitHub Issue #6. Until then, JPA database
auto-configuration is intentionally disabled so the application can start without
a database.
