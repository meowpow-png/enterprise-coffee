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

Start the supporting services:

```shell
just compose up -d
```

Build the application:

```shell
./gradlew build
```

Run the application:

```shell
./gradlew bootRun
```

The backend is available on port `8081`.

Open Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

See [development](#development) for more information.

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

**Example**

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

The backend is distributed as a Docker image based on Alpine 3.23. It requires access to a PostgreSQL database and an Enterprise Coffee Machine service.

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
| `./gradlew build`            | Build the application         |
| `./gradlew bootRun`          | Run the application           |
| `./gradlew test`             | Run unit tests                |
| `./gradlew integrationTest`  | Run integration tests         |
| `./gradlew jacocoTestReport` | Generate code coverage report |
| `just compose <args>`        | Run docker compose commands   |

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

When running the complete test suite, unit tests execute first, followed by integration tests. `jacocoTestReport` task runs last and aggregates code coverage from both test suites into a single report.

Both unit and integration tests are always executed regardless of Gradle's up-to-date checks to ensure test results and coverage reports are generated from a fresh test run.
