package io.github.meowpowpng.enterprisecoffee.coffee.api.exception;

/**
 * Indicates that a coffee order cannot be processed.
 */
public class CoffeeOrderProcessingException extends CoffeeOrderException {

    public CoffeeOrderProcessingException(String message) {
        super(message);
    }

    public CoffeeOrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
