package io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception;

/**
 * Indicates that a coffee order request is invalid.
 */
public class CoffeeOrderInvalidException extends CoffeeOrderException {

    /**
     * Creates a new invalid order exception.
     *
     * @param message exception message
     */
    public CoffeeOrderInvalidException(String message) {
        super(message);
    }
}
