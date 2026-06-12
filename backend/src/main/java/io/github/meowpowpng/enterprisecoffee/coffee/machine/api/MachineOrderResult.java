package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

/**
 * Result of a coffee order request submitted to the machine.
 */
public enum MachineOrderResult {

    /**
     * Represents the result of an order that was accepted.
     */
    ACCEPTED,

    /**
     * Represents the result of an order
     * that was rejected because the machine is busy.
     */
    BUSY,

    /**
     * Represents the result of an order
     * that was rejected because the request is invalid.
     */
    INVALID
}
