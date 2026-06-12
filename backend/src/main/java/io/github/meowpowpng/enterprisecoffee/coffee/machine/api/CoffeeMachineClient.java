package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

/**
 * Client for communicating with the coffee machine.
 */
public interface CoffeeMachineClient {

    /**
     * Returns the current operational
     * status of the coffee machine.
     *
     * @throws CoffeeMachineUnavailableException if communication with the machine fails
     * @throws CoffeeMachineProtocolException if the machine returns an invalid response
     */
    CoffeeMachineStatus status();

    /**
     * Returns the current coffee brewing
     * progress reported by the machine.
     *
     * @throws CoffeeMachineUnavailableException if communication with the machine fails
     * @throws CoffeeMachineProtocolException if the machine returns an invalid response
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
     * @throws CoffeeMachineUnavailableException if communication with the machine fails
     * @throws CoffeeMachineProtocolException if the machine returns an invalid response
     */
    MachineOrderResult order(CoffeeType type);
}
