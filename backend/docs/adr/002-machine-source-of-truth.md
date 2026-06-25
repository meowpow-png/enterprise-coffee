# ADR: Coffee Machine as the Source of Truth

## Context

The backend needs to expose the coffee machine's current status and brewing progress while also maintaining a history of orders and brewing jobs.

The question was whether the backend should treat the database as the authoritative source of this information or query the coffee machine directly.

## Alternatives

The obvious alternative was to persist the machine's state and use the database to answer status and progress requests. This would reduce calls to the coffee machine and centralize all state within the backend.

The downside is that the backend would be treating the database as authoritative for information it does not actually own.

## Decision

I decided that the coffee machine is the source of truth for its operational state. The backend always queries the machine for live information such as status and brewing progress.

The database is used to record business outcomes for auditing, debugging, and historical reporting.

## Rationale

The coffee machine is the component that actually knows whether it is brewing coffee and how far that process has progressed. Persisting that information in the database does not change ownership of the state.

Treating the database as authoritative would mean the backend is pretending to know something it does not actually control. Even if persistence never failed, I would still query the coffee machine for its current state.

## Consequences

**Benefits:**

- Live status and progress always come from the component that owns them.
- Persistence failures do not affect the ability to query the machine.
- The database remains focused on auditing and historical reporting.

**Limitations:**

- Status and progress requests depend on the coffee machine being available.
- The backend cannot serve live machine state from its own database.

**Implications:**

- Business logic should not rely on persisted machine state as the current source of truth.
- Persisted orders and jobs should be treated as historical records rather than operational state.
