package io.github.meowpowpng.enterprisecoffee.coffee.api;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

import java.util.Objects;

/**
 * Coffee order request submitted by client.
 */
public record ClientOrderRequest(CoffeeType type) {

    /**
     * Creates a new coffee order request.
     *
     * @param type coffee beverage to order from the machine
     *
     * @throws NullPointerException if {@code type} is {@code null}
     */
    public ClientOrderRequest {
        Objects.requireNonNull(type, "type must not be null");
    }
}
