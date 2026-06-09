# Enterprise Coffee Machine

Enterprise Coffee Machine is a configurable HTTP service that simulates a coffee machine.

The service accepts coffee orders, reports brewing progress, 
and exposes machine status through a simple REST API.

## Quick Start

Build the application:

```shell
just build
```

Run the application:

```shell
just run
```

Submit an order:

```shell
just order <coffee-type>
```

Watch brewing progress:

```shell
just watch-progress
```

See [commands](#commands) for more information.

## Endpoints

| Method | Endpoint    | Description              |
|--------|-------------|--------------------------|
| `GET`  | `/health`   | Service health status    |
| `GET`  | `/status`   | Current machine status   |
| `GET`  | `/progress` | Current brewing progress |
| `POST` | `/order`    | Submit coffee order      |

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

## Development

### Requirements

* Go
* Just

### Commands

| Command               | Description                   |
|-----------------------|-------------------------------|
| `just build`          | Build the application         |
| `just run`            | Build and run the application |
| `just status`         | Query machine status          |
| `just progress`       | Query brewing progress        |
| `just watch-progress` | Watch brewing progress        |
| `just order <type>`   | Submit coffee order           |
| `just clean`          | Remove build artifacts        |

See `config.json` for configured coffee types and brewing durations.
