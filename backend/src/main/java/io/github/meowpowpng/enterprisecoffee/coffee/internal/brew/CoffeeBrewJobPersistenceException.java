package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

/**
 * Indicates that an error occurred while
 * persisting or retrieving a coffee-brewing job.
 */
final class CoffeeBrewJobPersistenceException extends RuntimeException {

    /**
     * Creates a new persistence exception.
     *
     * @param message exception message
     */
    CoffeeBrewJobPersistenceException(String message) {
        super(message);
    }

    /**
     * Creates a new persistence exception.
     *
     * @param message exception message
     * @param cause underlying cause
     */
    CoffeeBrewJobPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
