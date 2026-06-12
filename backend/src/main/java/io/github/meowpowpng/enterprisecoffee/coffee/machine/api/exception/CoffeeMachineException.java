package io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception;

/**
 * Base class for machine communication exceptions.
 */
public abstract class CoffeeMachineException extends RuntimeException {

    /**
     * Creates new coffee machine exception.
     *
     * @param message exception detail message
     * @param cause underlying failure
     */
    CoffeeMachineException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates new coffee machine exception.
     *
     * @param message exception detail message
     */
    CoffeeMachineException(String message) {
        super(message);
    }
}
