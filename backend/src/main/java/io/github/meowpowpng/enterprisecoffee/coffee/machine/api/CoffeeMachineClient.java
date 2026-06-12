package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

/**
 * Client for communicating with the coffee machine.
 */
public interface CoffeeMachineClient {

    /**
     * Returns the current operational
     * status of the coffee machine.
     *
     * @throws CoffeeMachineException if communication with the
     * machine fails, or the machine returns an invalid response
     */
    CoffeeMachineStatus status();

    /**
     * Returns the current coffee brewing
     * progress reported by the machine.
     *
     * @throws CoffeeMachineException if communication with the
     * machine fails, or the machine returns an invalid response
     */
    MachineCoffeeProgress progress();

    /**
     * Submits a coffee order to the coffee machine.
     *
     * @param type coffee beverage to prepare
     *
     * @return result of the coffee order request
     *
     * @throws NullPointerException if {@code type} is {@code null}
     * @throws CoffeeMachineException if communication with the machine fails
     * @throws IllegalStateException if the machine returns an unexpected response
     */
    MachineOrderResult order(CoffeeType type);
}
