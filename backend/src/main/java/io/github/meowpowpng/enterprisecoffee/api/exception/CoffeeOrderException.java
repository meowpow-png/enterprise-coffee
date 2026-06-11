package io.github.meowpowpng.enterprisecoffee.api.exception;

/**
 * Base class for coffee order processing failures.
 */
abstract class CoffeeOrderException extends RuntimeException {

    /**
     * Creates a new coffee order exception.
     *
     * @param message exception detail message
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    CoffeeOrderException(String message) {
        super(message);
    }

    /**
     * Creates a new coffee order exception.
     *
     * @param message exception detail message
     * @param cause underlying cause
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    CoffeeOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
