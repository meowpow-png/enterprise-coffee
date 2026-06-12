package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import java.util.Objects;

/**
 * Current operational status
 * reported by the coffee machine.
 */
record MachineStatusResponse(String status) {

    /**
     * Creates a new machine status response.
     *
     * @param status operational status of the coffee machine
     *
     * @throws NullPointerException if {@code status} is {@code null}
     */
    MachineStatusResponse {
        Objects.requireNonNull(status, "status must not be null");
    }
}
