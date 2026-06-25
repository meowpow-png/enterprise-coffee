# ADR: Use Domain Events for Workflow Orchestration

## Context

The order workflow doesn't end when the coffee machine accepts or rejects an order. Accepted orders need to be persisted, brewing jobs need to be created, and progress needs to be tracked in the background.

My initial implementation kept all of this inside the order service, but it quickly became the place where every new responsibility was added.

## Alternatives

The simplest approach was to let the order service orchestrate the entire workflow by directly calling persistence and job tracking components.

While this would have kept the control flow straightforward, it also meant the order service would continue growing as new workflow stages were introduced.

## Decision

I decided to split the workflow into independent stages connected by domain events. Each stage performs a single business responsibility, publishes the outcome, and leaves the next stage to react independently.

## Rationale

The main motivation was keeping responsibilities separate. Once the machine has accepted or rejected an order, the order service has finished its job. Everything that follows is simply a consequence of that business outcome.

This also allows the client to receive a response immediately while persistence and background processing continue independently. At the same time, the database becomes an audit trail of what happened rather than the source of truth for the machine's current state.

## Consequences

**Benefits:**

- The order service remains focused on a single responsibility.
- The client receives a response before auditing and background processing complete.
- New workflow stages can be introduced without modifying existing components.

**Limitations:**

- The control flow is less obvious because it spans multiple event handlers.
- Debugging the workflow requires following event publication and consumption.

**Implications:**

- Workflow stages should communicate through business events rather than direct service calls.
- New responsibilities should be implemented by subscribing to existing events where appropriate instead of extending the order service.
