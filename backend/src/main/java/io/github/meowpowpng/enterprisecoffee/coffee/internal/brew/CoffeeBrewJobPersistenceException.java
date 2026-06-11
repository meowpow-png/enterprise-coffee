package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

/**
 * Indicates that an error occurred while
 * persisting or retrieving a coffee-brewing job.
 */
public class CoffeeBrewJobPersistenceException extends RuntimeException {

    public CoffeeBrewJobPersistenceException(String message) {
        super(message);
    }

    public CoffeeBrewJobPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
