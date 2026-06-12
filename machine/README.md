# Enterprise Coffee Machine

Enterprise Coffee Machine is a configurable HTTP service that simulates a coffee machine.

The service accepts coffee orders, reports brewing progress, 
and exposes machine status through a simple REST API.

## Quick Start

Build the application:

```shell
just machine-build
```

Run the application:

```shell
just machine-run
```

Submit an order:

```shell
just machine-order <coffee-type>
```

Watch brewing progress:

```shell
just machine-watch-progress
```

See [commands](#commands) for more information.

## API

### Send Coffee Order

Submit a brewing order for a specific coffee type.

**Path**

```text
POST /order
```

**Body (application/json)**

| Field | Type   | Required | Description         |
|-------|--------|----------|---------------------|
| type  | string | yes      | Coffee type to brew |

**Example:**

```json
{
  "type": "ESPRESSO"
}
```

**Responses**

- `202 Accepted` - Order accepted and brewing started
- `409 Conflict` - Order rejected because the machine is busy
- `400 Bad Request` - Invalid JSON payload or unsupported coffee type

### Get Brewing Progress

Returns current brewing progress for the active coffee.

**Path**

```text
GET /progress
```

**Response (application/json)**

| Field     | Type    | Description                         |
|-----------|---------|-------------------------------------|
| type      | string  | Current coffee type being brewed    |
| progress  | integer | Brewing progress (0–100)            |

**Example:**

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

### Get Machine Status

Returns current machine state.

**Path**

```text
GET /status
```

**Response (application/json)**

| Field  | Type   | Description                             |
|--------|--------|-----------------------------------------|
| status | string | Current machine status (READY, BREWING) |

**Example:**

```json
{
  "status": "BREWING"
}
```

### Health Check

Returns service health state.

**Path**

```text
GET /health
```

**Responses**

- `200 OK` - Service is healthy
- `503 Service Unavailable` - Service is unhealthy

## Configuration

Configuration is loaded from `config.json`.

**Example:**

```json
{
  "machine": {
    "name": "BrewMaster 3000"
  },
  "server": {
    "port": 8080
  },
  "coffee": {
    "types": [
      {
        "name": "ESPRESSO",
        "duration": "10s"
      },
      {
        "name": "AMERICANO",
        "duration": "15s"
      },
      {
        "name": "CAPPUCCINO",
        "duration": "20s"
      }
    ]
  }
}
```

| Property                  | Description                       |
|---------------------------|-----------------------------------|
| `machine.name`            | Machine name displayed at startup |
| `server.port`             | HTTP server port                  |
| `coffee.types[].name`     | Supported coffee type             |
| `coffee.types[].duration` | Brewing duration                  |

## CLI Options

The following command line options are available:

| Option     | Description             | Default       |
|------------|-------------------------|---------------|
| `--port`   | HTTP server port        | `8080`        |
| `--config` | Configuration file path | `config.json` |

**Example:**

Run the service with a custom configuration file and port:

```shell
./build/bin/machine \
  --config=custom-config.json \
  --port=9090
```

## Deployment

Build the application:

```shell
just machine-build
```

Build and start the service:

```shell
just machine-compose up --build
```

Stop the service:

```shell
just machine-compose down
```

The service is available on port `8080` by default.

## Development

### Requirements

* Go 1.25 or later
* Just >= 1.50

### Commands

| Command                       | Description                            |
|-------------------------------|----------------------------------------|
| `just machine-build`          | Build the application                  |
| `just machine-run`            | Build and run the application          |
| `just machine-test` <args>    | Run unit tests with race detection     |
| `just machine-test-coverage`  | Run tests and generate coverage report |
| `just machine-status`         | Query machine status                   |
| `just machine-progress`       | Query brewing progress                 |
| `just machine-watch-progress` | Watch brewing progress in real time    |
| `just machine-order <type>`   | Submit coffee order                    |
| `just machine-clean`          | Remove build artifacts                 |
| `just machine-compose <args>` | Run docker compose commands            |

See `config.json` for configured coffee types and brewing durations.
