package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import java.util.Objects;

/**
 * Error response returned by the coffee machine.
 */
record MachineErrorResponse(String message) {

    /**
     * Creates a new machine error response.
     *
     * @param message error description
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    MachineErrorResponse {
        Objects.requireNonNull(message, "message must not be null");
    }
}
