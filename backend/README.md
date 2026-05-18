# Clean It Backend

Spring Boot backend for the **Clean It!** technical task.

This service implements the backend for two focused use cases in a textile cleaning scenario:

- receiving textiles and creating a cleaning order
- searching and managing cleaning orders for a local shop manager

The application is intentionally scoped as a prototype. Persistence is simulated in memory, authentication is not implemented, and the API is centered on the two requested demo flows.

## Tech Stack

- Java 25
- Spring Boot 4
- Spring Web MVC
- Bean Validation
- Spring Boot Actuator
- springdoc OpenAPI / Swagger UI
- Maven Wrapper
- Spotless for Java formatting checks
- JUnit 5 and Spring Boot test utilities

## Implemented Scope

### Use Case 1: Order Reception

The backend supports creating a cleaning order for a new or existing customer.

Implemented behavior:

- validates the incoming order request
- resolves an existing customer by id, or falls back to matching by email or `phoneNumber`
- creates a new customer if no match exists
- generates order numbers and textile item codes
- derives item prices from selected cleaning services
- calculates the total order price
- stores the new order in memory with initial status `RECEIVED`

### Use Case 2: Manager Order Overview

The backend supports searching and updating shop orders.

Implemented behavior:

- search by shop code
- optional free-text search by order number or customer name
- optional status filtering
- pagination with page metadata
- status transitions with business validation

## Current Business Rules

### Order Creation

- `shopCode` is required.
- `expectedPickupDate` is required and must be today or later.
- at least one textile item is required
- at most 20 textile rows are allowed per order
- each textile row must have:
    - a textile type
    - a quantity between 1 and 20
    - at least one cleaning service
    - at most 10 selected cleaning services
- the customer must provide:
    - a display name
    - at least one contact channel: email or phone number

### Expected Pickup Date Rule

The expected pickup date is validated against the total selected item quantity.

Rule:

- the pickup date must equal `today + itemCount`
- at least one day is always added

Example:

- 1 textile item total -> pickup date must be tomorrow
- 3 textile items total -> pickup date must be three days from today

### Order Status Transitions

Allowed transitions:

- `RECEIVED` -> `IN_CLEANING`
- `RECEIVED` -> `CANCELLED`
- `IN_CLEANING` -> `READY_FOR_PICKUP`
- `IN_CLEANING` -> `CANCELLED`
- `READY_FOR_PICKUP` -> `RETURNED`
- `READY_FOR_PICKUP` -> `CANCELLED`

Terminal states:

- `RETURNED`
- `CANCELLED`

If an invalid transition is requested, the API returns `409 Conflict`.

## Seeded Demo Data

The in-memory setup currently includes:

- 1 shop
- 3 customers
- 5 example orders

Default assigned demo shop:

```txt
VIE-01
Clean It! Vienna, 1010, Schottentor 3
```

The assigned shop endpoint always resolves to this demo shop in the current implementation.

## API Base Path

All endpoints are exposed under:

```txt
/api/v1
```

## API Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/shops/assigned` | Return the assigned demo shop |
| `GET` | `/cleaning-services` | Return the available cleaning service catalog |
| `GET` | `/customers` | Search existing customers |
| `POST` | `/orders` | Create a new cleaning order |
| `GET` | `/orders` | Search orders for a shop |
| `PATCH` | `/orders/{orderId}/status` | Change the status of an order |

## Request and Response Overview

### `GET /api/v1/shops/assigned`

Returns the assigned demo shop.

Example response:

```json
{
  "shopCode": "VIE-01",
  "name": "Clean It! Vienna, 1010, Schottentor 3"
}
```

### `GET /api/v1/cleaning-services`

Returns the current service catalog.

Configured service codes:

- `STANDARD_CLEANING`
- `DRY_CLEANING`
- `IRONING`
- `STAIN_REMOVAL`
- `EXPRESS_SERVICE`

Prices are returned in minor units.

Example response item:

```json
{
  "code": "STANDARD_CLEANING",
  "displayName": "Standard cleaning",
  "priceMinorUnits": 990
}
```

### `GET /api/v1/customers`

Supported query parameters:

- `displayName`
- `email`
- `phoneNumber`

Behavior:

- if no search term is provided, the endpoint returns an empty list
- matching is case-insensitive
- results are limited to 10 items

Example:

```http
GET /api/v1/customers?displayName=max
```

### `POST /api/v1/orders`

Creates a new order.

Example request:

```json
{
  "shopCode": "VIE-01",
  "expectedPickupDate": "2026-05-20",
  "customer": {
    "displayName": "Max Mustermann",
    "email": "max.mustermann@example.at",
    "phoneNumber": "+49 660 0000001"
  },
  "items": [
    {
      "textileType": "SHIRT",
      "quantity": 2,
      "material": "Cotton",
      "serviceCodes": [
        "STANDARD_CLEANING",
        "IRONING"
      ]
    }
  ]
}
```

Example response:

```json
{
  "id": "104d2c8a-d112-496a-824d-77ae7b0c90e9",
  "orderNumber": "CI-2026-0001",
  "customerDisplayName": "Max Mustermann",
  "shopCode": "VIE-01",
  "status": "RECEIVED",
  "receivedAt": "2026-05-08T10:15:00Z",
  "expectedPickupDate": "2026-05-10",
  "itemCount": 2,
  "totalPriceMinorUnits": 3470
}
```

### `GET /api/v1/orders`

Supported query parameters:

- `shopCode` required
- `query` optional
- `status` optional
- `page` optional, default `0`
- `size` optional, default `10`, maximum `100`

Search behavior:

- shop code must exist
- free-text search matches:
    - order number
    - customer display name
- results are sorted by `receivedAt` descending

Example:

```http
GET /api/v1/orders?shopCode=VIE-01&query=max&status=RECEIVED&page=0&size=10
```

Example response shape:

```json
{
  "items": [
    {
      "id": "b4c0f8d2-7f91-4f2e-9b3b-2a8b9c6a1005",
      "orderNumber": "CI-2026-0005",
      "customerDisplayName": "Anna Huber",
      "shopCode": "VIE-01",
      "status": "RECEIVED",
      "receivedAt": "2026-05-09T11:10:00Z",
      "expectedPickupDate": "2026-05-13",
      "itemCount": 1,
      "totalPriceMinorUnits": 1480
    }
  ],
  "page": 0,
  "size": 10,
  "totalItems": 1,
  "totalPages": 1
}
```

### `PATCH /api/v1/orders/{orderId}/status`

Example request:

```json
{
  "targetStatus": "IN_CLEANING"
}
```

Example behavior:

- validates the transition from the current status
- updates the order in memory
- sets `returnedAt` when the target status is `RETURNED`
- sets `cancelledAt` when the target status is `CANCELLED`

## Error Handling

The backend uses Spring Problem Details.

Typical responses:

- `404 Not Found`
- `409 Conflict`
- `422 Unprocessable Content`
- `500 Internal Server Error`

Validation responses use a structured `errors` property.

Example shape:

```json
{
  "type": "urn:problem-type:validation-error",
  "title": "Validation failed",
  "status": 422,
  "detail": "One or more fields are invalid.",
  "errors": [
    {
      "field": "expectedPickupDate",
      "message": "Expected pickup date must be 2026-05-20 based on the selected item quantity."
    }
  ]
}
```

## Project Structure

The code is organized by feature and shared concerns.

### `src/main/java/com/mk/backend/ordermanagement`

Business-specific code for the textile cleaning domain.

Key areas:

- `application`
    - service interfaces
    - business logic
    - validation
    - pricing
    - support components such as generators and service catalog
- `model`
    - domain entities
    - records
    - enums
- `persistence`
    - repository interfaces
    - in-memory implementations
- `web`
    - REST controllers
    - request DTOs
    - response DTOs
- `mapper`
    - mapping between domain and API responses

### `src/main/java/com/mk/backend/shared`

Shared infrastructure for the backend.

Key areas:

- `config`
    - `Clock`
    - shared validation setup
- `error`
    - custom exceptions
    - global exception handling
- `web`
    - generic page response wrapper

## Prerequisites

- Java 25
- Maven is not required globally because the project uses the Maven Wrapper

The repository includes a Java version file:

```txt
backend/.java-version
```

Current configured version:

```txt
25.0.3
```

## Local Development

Run the backend from the `backend` directory:

```bash
./mvnw spring-boot:run
```

Default local URL:

```txt
http://localhost:8080
```

Useful endpoints:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Health: <http://localhost:8080/actuator/health>
- Liveness: <http://localhost:8080/actuator/health/liveness>
- Readiness: <http://localhost:8080/actuator/health/readiness>

## Configuration

Main application configuration:

```txt
src/main/resources/application.yaml
```

Supported environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | HTTP server port |
| `LOG_LEVEL_ROOT` | `info` | Root log level |
| `LOG_LEVEL_APP` | `info` | Application package log level |

Logging is configured for ECS-style structured console output.

Example override:

```bash
SERVER_PORT=9090 LOG_LEVEL_APP=debug ./mvnw spring-boot:run
```

## Build and Test

Run tests:

```bash
./mvnw test
```

Run full verification:

```bash
./mvnw clean verify
```

Run formatter:

```bash
./mvnw spotless:apply
```

Formatting is enforced during the Maven verify phase.

## Current Automated Tests

The backend currently contains tests for:

- Spring context startup
- order validation rules
- pricing calculations
- order status side effects on the domain model

This is useful baseline coverage, but it is still lighter than a production-grade test suite.

## Containerization

Build the backend image from the `backend` directory:

```bash
docker build -t clean-it-backend:local .
```

The Docker image uses a multi-stage build:

- build stage: `eclipse-temurin:25-jdk`
- runtime stage: `eclipse-temurin:25-jre`

The container exposes port:

```txt
8080
```

## Docker Compose Integration

The repository root contains a compose file for the full stack.

Start the full stack from the repository root:

```bash
docker compose up --build
```

Current port mapping:

- backend: <http://localhost:8081>
- frontend: <http://localhost:4200>

## CI

GitHub Actions workflow:

```txt
.github/workflows/ci.yml
```

Current pipeline stages:

- backend verify
- frontend lint, test, and build
- backend Docker image build
- frontend Docker image build

The CI workflow uses Java 25 for backend builds.

## Limitations

- no database integration
- no persistence across restarts
- no authentication or authorization
- one fixed demo shop
- seeded in-memory demo data
- no multishop user context
- optimized for a technical task demo, not for production use

## Future Extensions

Natural next steps for this backend would be:

- add persistent storage
- add authentication and authorization
- derive assigned shop from the authenticated user
- introduce integration and controller tests
- add deployment configuration for Azure Container Apps
- extend the domain to cover textile return and customer management beyond the current scope
