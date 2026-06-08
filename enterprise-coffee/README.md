## Enterprise Coffee Platform

Enterprise Coffee Platform is a fictional system responsible 
for the highly critical task of delivering espresso to office employees.

## Overview

Before a coffee request can be fulfilled, the platform must verify that the 
machine is online, available, and ready to brew. If the machine is unavailable or 
undergoing maintenance, the request is rejected and an appropriate response is returned.

This example demonstrates how GatlingFx can be used to validate application 
and infrastructure behavior in a small service-based environment built 
around a problem that probably didn't need a service-based environment.

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
Before submitting a brew request, the backend verifies that 
the machine is available and ready to prepare coffee.

### coffee-machine

External API representing a physical coffee machine. The machine reports
its current status and accepts brew requests when it is ready to do so.

## Requirements

- Docker
- Java 21
