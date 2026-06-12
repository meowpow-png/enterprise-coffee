package io.github.meowpowpng.enterprisecoffee.coffee.internal.client;

import java.util.Objects;

/**
 * Coffee order request submitted to the coffee machine.
 */
record MachineOrderRequest(String type) {

    /**
     * Creates a new machine brewing request.
     *
     * @param type coffee beverage to prepare
     *
     * @throws NullPointerException if {@code type} is null
     */
    MachineOrderRequest {
        Objects.requireNonNull(type, "type must not be null");
    }
}
