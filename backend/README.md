# Enterprise Coffee Backend

Enterprise Coffee Backend is the core application of the Enterprise Coffee Platform.

The service is responsible for handling coffee requests and coordinating interactions with the coffee machine.

## Quick Start

Requirements:

* Java 21
* Docker
* Just >= 1.50

Copy `.env.example` to `.env` and adjust the values as needed. For example:

```text
DB_HOST=localhost
DB_NAME=coffee
DB_USER=coffee
DB_PASSWORD=coffee
```

Start supporting services:

```shell
just compose up -d
```

Build backend application:

```shell
./gradlew build
```

Run backend application:

```shell
./gradlew bootRun
```

The backend is available on port `8081`.

Open Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

See [Development](#development) section for more information.

## Architecture

The backend follows a modular, domain-oriented architecture inspired by practices commonly used in enterprise applications. While the coffee domain is intentionally simple, the project emphasizes clear separation of responsibilities and maintainable code organization.

Read [ARCHITECTURE.md](docs/ARCHITECTURE.md) for more information.

## API

### Submit Coffee Order

Submits a coffee order.

**Path**

```text
POST /order
```

**Body (application/json)**

| Field | Type   | Required | Description          |
|-------|--------|----------|----------------------|
| type  | string | yes      | Coffee type to order |

**Response (application/json)**

| Field   | Type   | Description                      |
|---------|--------|----------------------------------|
| message | string | Human-readable result message    |

**Example**

```json
{
  "type": "ESPRESSO"
}
```

**Responses**

- `202 Accepted` - Coffee order accepted
- `400 Bad Request` - Invalid request or unsupported coffee type
- `409 Conflict` - Coffee order could not be processed

**Example**

```json
{
  "message": "Coffee order was successfully accepted"
}
```

### Get Coffee Orders

Returns the latest coffee orders.

**Path**

```text
GET /orders
```

**Query Parameters**

| Parameter | Type    | Required | Description                                               |
|-----------|---------|----------|-----------------------------------------------------------|
| limit     | integer | no       | Maximum number of coffee orders to return (20 by default) |

**Response (application/json)**

| Field  | Type  | Description   |
|--------|-------|---------------|
| orders | array | Coffee orders |

**Coffee Order**

| Field     | Type   | Description                       |
|-----------|--------|-----------------------------------|
| id        | string | Coffee order identifier           |
| type      | string | Requested coffee type             |
| status    | string | Current coffee order status       |
| createdAt | string | Time the coffee order was created |

**Example**

```json
{
  "orders": [
    {
      "id": "a4b84783-b19d-4af0-8e2d-3d68552f9d67",
      "type": "ESPRESSO",
      "status": "ACCEPTED",
      "createdAt": "2026-01-01T10:02:00Z"
    },
    {
      "id": "fb4a75d4-8ca7-47b8-9d2d-d87e489e51b2",
      "type": "LATTE",
      "status": "FAILED",
      "createdAt": "2026-01-01T10:01:00Z"
    }
  ]
}
```

**Responses**

- `200 OK` - Coffee orders returned successfully
- `400 Bad Request` - Invalid request

### Get Machine Status

Returns the current machine status.

**Path**

```text
GET /status
```

**Response (application/json)**

| Field  | Type   | Description            |
|--------|--------|------------------------|
| status | string | Current machine status |

**Example**

```json
{
  "status": "BREWING"
}
```

**Responses**

- `200 OK` - Machine status returned successfully
- `503 Service Unavailable` - Machine is unavailable
- `500 Internal Server Error` - Invalid response received from the machine

### Get Brewing Progress

Returns current coffee brewing progress.

**Path**

```text
GET /progress
```

**Response (application/json)**

| Field    | Type    | Description                         |
|----------|---------|-------------------------------------|
| type     | string  | Current coffee type (empty if idle) |
| progress | integer | Brewing progress (0–100)            |

**Examples**

Espresso:

```json
{
  "type": "ESPRESSO",
  "progress": 42
}
```

Idle machine:

```json
{
  "type": "",
  "progress": 0
}
```

**Responses**

- `200 OK` - Brewing progress returned successfully
- `503 Service Unavailable` - Machine is unavailable
- `500 Internal Server Error` - Invalid response received from the machine

## Configuration

Configuration is loaded from `application.properties`.

**Example:**

```properties
coffee.machine.base-url=http://localhost:8082
coffee.machine.connect-timeout=2s
coffee.machine.read-timeout=2s

coffee.order.job.polling-interval=500ms
coffee.order.job.timeout=30s
```

| Property                            | Description                                                              |
|-------------------------------------|--------------------------------------------------------------------------|
| `coffee.machine.base-url`           | Base URL of the coffee machine service                                   |
| `coffee.machine.connect-timeout`    | Timeout for establishing a connection to the coffee machine              |
| `coffee.machine.read-timeout`       | Timeout for waiting on a response from the coffee machine                |
| `coffee.order.job.polling-interval` | Interval between consecutive progress checks while tracking a coffee job |
| `coffee.order.job.timeout`          | Maximum time to track a coffee job before marking it as failed           |

## Deployment

The backend is distributed as a Docker image based on Alpine 3.23. It requires access to a PostgreSQL database and Enterprise Coffee Machine service.

The service is configured using the following environment variables:

| Variable           | Description                       |
|--------------------|-----------------------------------|
| `DB_HOST`          | Hostname of the PostgreSQL server |
| `DB_NAME`          | PostgreSQL database name          |
| `DB_USER`          | PostgreSQL username               |
| `DB_PASSWORD`      | PostgreSQL user password          |
| `MACHINE_BASE_URL` | Base URL of the machine service   |
| `COFFEE_LOG_LEVEL` | Application logging level         |

**Example `compose.yml`:**

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: coffee
      POSTGRES_USER: coffee
      POSTGRES_PASSWORD: coffee
    volumes:
      - postgres-data:/var/lib/postgresql/data

  machine:
    image: meowpow-png/enterprise-coffee-machine

  backend:
    image: meowpow-png/enterprise-coffee-backend
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      DB_HOST: postgres
      DB_NAME: coffee
      DB_USER: coffee
      DB_PASSWORD: coffee
      MACHINE_BASE_URL: http://machine:8080
      COFFEE_LOG_LEVEL: INFO
    ports:
      - "8081:8081"

volumes:
  postgres-data:
```

**Notes:**

- The backend listens on port `8081` inside the container. Configure networking and port mappings as required by your deployment environment.
- The deployment examples use PostgreSQL 16, which is the supported database version for the current release.
- The machine service listens on port `8080` inside the container by default. Unless configured otherwise, there is no need to expose or remap this port.

## Development

### Requirements

* Java 21
* Docker
* Just >= 1.50
* IntelliJ IDEA (recommended)

### Commands

| Command                      | Description                   |
|------------------------------|-------------------------------|
| `just compose <args>`        | Run Docker Compose commands   |
| `just status`                | Get coffee machine status     |
| `just order <type>`          | Submit coffee order           |
| `just orders [limit]`        | Get latest coffee orders      |
| `just progress`              | Get brewing progress          |

### Docker

Build backend jar:

```shell
./gradlew build
```

Build Docker image:

```shell
just compose build
```

Start local container stack:

```shell
just compose up -d
```

**Notes:**

- The Docker image is based on Alpine 3.23 to reduce image size
- The image includes a custom Java runtime created with `jlink` and contains only the modules required to run the application
- The Dockerfile expects a pre-built application JAR and does not build the project

### Testing

Run unit tests:

```shell
./gradlew test
```

Run integration tests:

```shell
./gradlew integrationTest
```

Generate a combined code coverage report:

```shell
./gradlew jacocoTestReport
```

**Notes:**

- When running all test suites, unit tests execute first, followed by integration tests
- Code coverage task runs last and aggregates coverage from all suites into a single report
- Both unit and integration tests are always executed regardless of Gradle's up-to-date checks to ensure test results and coverage reports are generated from a fresh test run
- See [TESTING.md](docs/TESTING.md) for testing architecture, infrastructure, and project conventions
