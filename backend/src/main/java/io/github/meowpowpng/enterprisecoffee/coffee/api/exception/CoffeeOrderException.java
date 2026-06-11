package io.github.meowpowpng.enterprisecoffee.coffee.api.exception;

/**
 * Base class for coffee order processing failures.
 */
abstract class CoffeeOrderException extends RuntimeException {

    /**
     * Creates a new coffee order exception.
     *
     * @param message exception detail message
     */
    CoffeeOrderException(String message) {
        super(message);
    }

    /**
     * Creates a new coffee order exception.
     *
     * @param message exception detail message
     * @param cause underlying cause
     */
    CoffeeOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
