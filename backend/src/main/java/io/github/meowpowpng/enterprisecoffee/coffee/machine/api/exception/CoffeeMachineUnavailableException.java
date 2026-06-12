package io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception;

/**
 * Indicates that the coffee machine cannot be reached.
 */
public class CoffeeMachineUnavailableException extends CoffeeMachineException {

    /**
     * Creates new machine-unavailable exception.
     *
     * @param message exception message
     */
    public CoffeeMachineUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
