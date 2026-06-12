package io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception;

/**
 * Indicates that the coffee machine
 * returns an invalid or unexpected response.
 */
public class CoffeeMachineProtocolException extends CoffeeMachineException {

    /**
     * Creates new machine protocol exception.
     *
     * @param message exception message
     */
    public CoffeeMachineProtocolException(String message) {
        super(message);
    }

    /**
     * Creates new machine protocol exception.
     *
     * @param message exception message
     * @param cause underlying cause
     */
    public CoffeeMachineProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
