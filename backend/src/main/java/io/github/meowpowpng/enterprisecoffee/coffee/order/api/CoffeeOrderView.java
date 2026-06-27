package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * View of a coffee order.
 */
public record CoffeeOrderView(
        UUID id,
        String type,
        String status,
        Instant createdAt
) {

    /**
     * Creates a new coffee order view.
     *
     * @param id unique coffee order identifier
     * @param type requested coffee type
     * @param status current coffee order status
     * @param createdAt timestamp when the coffee order was created
     *
     * @throws NullPointerException if any argument is {@code null}
     */
    public CoffeeOrderView {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }
}
