package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import java.util.Objects;

/**
 * Result of a coffee order request.
 */
public record ClientOrderResponse(String message) {

    /**
     * Creates a new coffee order response.
     *
     * @param message human-readable description of the result
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    public ClientOrderResponse {
        Objects.requireNonNull(message, "message must not be null");
    }

    public static ClientOrderResponse accepted() {
        return new ClientOrderResponse("Coffee order was successfully accepted");
    }
}
