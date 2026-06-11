package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

/**
 * Indicates that a coffee-brewing job could not be mapped.
 */
class CoffeeBrewJobMappingException extends RuntimeException {

    /**
     * Creates a new mapping exception.
     *
     * @param message exception detail message
     * @param cause underlying cause
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    CoffeeBrewJobMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
