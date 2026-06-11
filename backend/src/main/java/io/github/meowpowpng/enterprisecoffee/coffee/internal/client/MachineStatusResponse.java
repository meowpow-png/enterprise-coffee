package io.github.meowpowpng.enterprisecoffee.coffee.internal.client;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeMachineStatus;

import java.util.Objects;

/**
 * Current operational status
 * reported by the coffee machine.
 */
public record MachineStatusResponse(CoffeeMachineStatus status) {

    /**
     * Creates a new machine status response.
     *
     * @param status operational status of the coffee machine
     *
     * @throws NullPointerException if {@code status} is {@code null}
     */
    public MachineStatusResponse {
        Objects.requireNonNull(status, "status must not be null");
    }
}
