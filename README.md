## Enterprise Coffee Platform

Enterprise Coffee Platform is a fictional system responsible
for the highly critical task of delivering espresso to office employees.

## Overview

Before a coffee request can be fulfilled, the platform must verify that the
machine is online and ready to brew. If the machine is unavailable or unable
to accept requests, the order is rejected and an appropriate response is returned.

## Components

```text
Employee
    |
    v
coffee-gateway
    |
    v
coffee-backend
    |
    v
coffee-machine
```

### coffee-gateway

Public entrypoint for the platform. The gateway is responsible for
routing coffee requests to the backend and exposing health endpoints.

### coffee-backend

Core application responsible for handling coffee requests.
The backend manages coffee orders and verifies that the
machine is available and ready to prepare coffee.

### coffee-machine

External API representing a physical coffee machine. The machine reports
its current status and accepts brew requests when it is able to do so.

## Requirements

- Docker
- Java 21
