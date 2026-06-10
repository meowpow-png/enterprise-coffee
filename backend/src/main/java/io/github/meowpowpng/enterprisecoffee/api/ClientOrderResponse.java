package io.github.meowpowpng.enterprisecoffee.api;

import io.github.meowpowpng.enterprisecoffee.model.CoffeeOrderStatus;

import java.util.Objects;

/**
 * Result of a coffee order request.
 */
public record ClientOrderResponse(CoffeeOrderStatus status, String message) {

    /**
     * Creates a new coffee order response.
     *
     * @param status outcome of the order processing operation
     * @param message human-readable description of the result
     *
     * @throws NullPointerException if {@code status} or {@code message} is {@code null}
     */
    public ClientOrderResponse {
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(message, "message must not be null");
    }

    public static ClientOrderResponse recieved() {
        return new ClientOrderResponse(
                CoffeeOrderStatus.RECEIVED,
                "Coffee order was successfully received"
        );
    }

    public static ClientOrderResponse dispatched() {
        return new ClientOrderResponse(
                CoffeeOrderStatus.DISPATCHED,
                "Coffee order was successfully dispatched"
        );
    }

    public static ClientOrderResponse completed() {
        return new ClientOrderResponse(
                CoffeeOrderStatus.COMPLETED,
                "Coffee order completed successfully"
        );
    }

    public static ClientOrderResponse rejected(String reason) {
        return new ClientOrderResponse(
                CoffeeOrderStatus.REJECTED,
                "Coffee order was rejected because " + reason
        );
    }

    public static ClientOrderResponse failed(String reason) {
        return new ClientOrderResponse(
                CoffeeOrderStatus.FAILED,
                "Coffee order failed because " + reason
        );
    }
}
