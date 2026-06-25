# ADR: Application-Owned Persistence Lifecycle

## Context

Repositories are responsible for persisting domain aggregates. The question was whether they should expose a single generic `save()` operation or require callers to explicitly state the lifecycle operation being performed.

## Alternatives

The obvious alternative was to expose a single `save()` method and allow the persistence framework to determine whether an aggregate should be created or updated.

While this reduces the repository API to a single operation, it also hides the caller's intent and delegates lifecycle decisions to the persistence framework.

## Decision

I decided that the application should explicitly control aggregate lifecycles. Repositories expose lifecycle-specific operations such as `create()` and `update()` instead of a generic `save()` method.

## Rationale

I prefer repository APIs that clearly express intent rather than relying on implicit behavior. Creating and updating are different operations with different expectations, and I want callers to explicitly state which one they intend to perform.

This also keeps lifecycle decisions within the application instead of delegating them to the persistence framework. Invalid lifecycle operations should fail explicitly rather than being inferred or silently handled.

## Consequences

**Benefits:**

- Repository APIs clearly communicate the caller's intent
- Aggregate lifecycle decisions remain under application control
- Invalid lifecycle operations fail explicitly

**Limitations:**

- Repository interfaces expose more operations than a generic CRUD API
- Additional persistence logic is required compared to relying on framework defaults

**Implications:**

- New repositories should expose explicit lifecycle operations instead of a generic `save()` method
- Callers are expected to know whether they are creating or updating an aggregate
