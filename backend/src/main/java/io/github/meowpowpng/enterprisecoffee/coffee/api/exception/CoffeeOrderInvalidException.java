package io.github.meowpowpng.enterprisecoffee.coffee.api.exception;

/**
 * Indicates that a coffee order request is invalid.
 */
public class CoffeeOrderInvalidException extends CoffeeOrderException {

    public CoffeeOrderInvalidException(String message) {
        super(message);
    }
}
