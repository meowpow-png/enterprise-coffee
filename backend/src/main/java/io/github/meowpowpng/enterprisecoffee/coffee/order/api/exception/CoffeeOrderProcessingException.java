package io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception;

/**
 * Indicates that a coffee order cannot be processed.
 */
public class CoffeeOrderProcessingException extends CoffeeOrderException {

    /**
     * Creates a new order processing exception.
     *
     * @param message exception message
     */
    public CoffeeOrderProcessingException(String message) {
        super(message);
    }

    /**
     * Creates a new order processing exception.
     *
     * @param message exception message
     * @param cause underlying cause
     */
    public CoffeeOrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
