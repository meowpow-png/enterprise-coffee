package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

/**
 * Exception thrown when communication
 * with the coffee machine fails.
 */
public final class CoffeeMachineException extends RuntimeException {

    /**
     * Creates a new coffee machine exception.
     *
     * @param message exception detail message
     * @param cause underlying failure
     */
   public CoffeeMachineException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new coffee machine exception.
     *
     * @param message exception detail message
     */
    public CoffeeMachineException(String message) {
        super(message);
    }
}
