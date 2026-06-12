package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

/**
 * Indicates that an error occurred while
 * persisting or retrieving a coffee job.
 */
final class CoffeeJobPersistenceException extends RuntimeException {

    /**
     * Creates a new persistence exception.
     *
     * @param message exception message
     */
    CoffeeJobPersistenceException(String message) {
        super(message);
    }

    /**
     * Creates a new persistence exception.
     *
     * @param message exception message
     * @param cause underlying cause
     */
    CoffeeJobPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
