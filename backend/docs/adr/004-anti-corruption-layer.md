# ADR: Anti-Corruption Layer

## Context

The backend integrates with external technologies such as the coffee machine over HTTP. The question was whether the rest of the application should communicate directly using technology-specific concepts or through interfaces expressed in the application's own language.

## Alternatives

The obvious alternative was to expose infrastructure concerns throughout the application by allowing components to work directly with HTTP clients, transport models, status codes, and protocol-specific exceptions.

While this reduces the amount of translation code, it also couples the application to implementation details of the external system.

## Decision

I decided that external systems should be accessed through anti-corruption layers that translate technology-specific concepts into the application's own domain language.

The coffee machine integration is implemented as one such layer, exposing business capabilities rather than HTTP operations.

## Rationale

I want the application to speak the language of the business domain rather than the language of the technologies it depends on. Infrastructure concerns such as HTTP, JSON payloads, transport exceptions, and protocol details belong at the system boundary.

This preserves semantic boundaries throughout the application. The rest of the codebase interacts with business concepts instead of external technologies, making those technologies implementation details rather than part of the application's model.

## Consequences

**Benefits:**

- Business logic remains independent of infrastructure technologies
- External systems can evolve without affecting the application's internal model
- Technology-specific concerns remain isolated at architectural boundaries

**Limitations:**

- Additional translation code is required between infrastructure and the application
- New integrations require dedicated adapter implementations

**Implications:**

- External technologies should be accessed through dedicated interfaces expressed in the application's own language
- Infrastructure-specific models and exceptions should not cross into the application layer
