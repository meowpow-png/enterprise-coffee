package io.github.meowpowpng.enterprisecoffee.coffee.internal.client;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

import java.util.Objects;

/**
 * Coffee order request submitted to the coffee machine.
 */
record MachineOrderRequest(CoffeeType type) {

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
