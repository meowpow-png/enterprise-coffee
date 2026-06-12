package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

/**
 * Indicates that an error occurred while
 * persisting or retrieving a coffee order.
 */
final class CoffeeOrderPersistenceException extends RuntimeException {

    /**
     * Creates a new persistence exception.
     *
     * @param message exception message
     * @param cause underlying cause
     */
    CoffeeOrderPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
