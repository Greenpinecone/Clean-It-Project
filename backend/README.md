# Clean It! Backend

Spring Boot backend for the Clean It! technical task.

This backend is part of a client-server application for selected textile cleaning business use cases, such as:

- viewing and interacting with cleaning orders as a manager
- creating or receiving cleaning orders as an employee

Persistence is currently planned as simulated in-memory storage to keep the implementation focused on the requested use cases.

## Tech Stack

- Java 25
- Spring Boot 4
- Maven Wrapper
- Spring Web MVC
- Bean Validation
- Spring Boot Actuator
- springdoc-openapi / Swagger UI
- JUnit 5, AssertJ, Mockito and Spring test utilities
- Spotless with Google Java Format for Java formatting checks

## Prerequisites

This project uses Java 25.

If you use `jenv`, the Java version is defined in:

```text
.java-version
```

Verify your local setup:

```bash
java -version
./mvnw -v
```

## Development

Start the backend locally:

```bash
./mvnw spring-boot:run
```

The backend starts on:

```text
http://localhost:8080
```

The port can be overridden through the `PORT` environment variable:

```bash
PORT=9090 ./mvnw spring-boot:run
```

## Health Check

Actuator health endpoint:

```text
http://localhost:8080/actuator/health
```

Liveness and readiness endpoints:

```text
http://localhost:8080/actuator/health/liveness
http://localhost:8080/actuator/health/readiness
```

These endpoints are intended for local checks and later container readiness/liveness probes.

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Swagger UI may be empty until the first REST controllers are implemented.

## Tests

Run backend tests:

```bash
./mvnw test
```

Run a full verification build:

```bash
./mvnw clean verify
```

The full verification build runs tests and formatting checks.

## Code Formatting

Format Java code:

```bash
./mvnw spotless:apply
```

Check Java formatting:

```bash
./mvnw spotless:check
```

Formatting checks are also bound to the Maven `verify` phase, so this command fails if the code is not formatted correctly:

```bash
./mvnw clean verify
```

## Architecture Notes

The backend exposes API endpoints below:

```text
/api/...
```

The frontend calls these endpoints through a reverse proxy:

```text
Browser -> Frontend -> /api -> Backend
```

In production, the backend is intended to be reachable internally from the frontend container/app, not directly from the public internet.

The backend is structured around business capabilities rather than technical layers. Feature packages such as `orders`, `customers`, or `shops` can contain their controller, service, domain model, repository abstraction, in-memory implementation, and request/response records close together.

## Configuration

Application configuration is located in:

```text
src/main/resources/application.yml
```

Current configuration covers:

- application name
- server port
- logging levels
- Actuator health endpoint exposure
- liveness/readiness probe support

Runtime configuration can be provided through environment variables.

Currently supported variables:

| Variable | Default | Purpose |
|---|---:|---|
| `PORT` | `8080` | Backend server port |
| `LOG_LEVEL_ROOT` | `info` | Root logging level |
| `LOG_LEVEL_APP` | `debug` | Application package logging level |

## Security

Spring Security is intentionally not configured in this initial version.

For this limited technical task, authentication and user context are simulated. In a real production system, authentication would be integrated through an identity provider such as OIDC/Entra ID and the backend API would be protected accordingly.
