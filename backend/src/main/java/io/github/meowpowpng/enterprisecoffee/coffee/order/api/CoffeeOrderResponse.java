package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import java.util.Objects;

/**
 * Result of a coffee order request.
 */
public record CoffeeOrderResponse(String message) {

    /**
     * Creates a new coffee order response.
     *
     * @param message human-readable description of the result
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    public CoffeeOrderResponse {
        Objects.requireNonNull(message, "message must not be null");
    }

    /**
     * Returns the standard response
     * for an accepted coffee order.
     */
    public static CoffeeOrderResponse accepted() {
        return new CoffeeOrderResponse("Coffee order was successfully accepted");
    }
}
