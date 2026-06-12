package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

/**
 * Indicates that a coffee order cannot be mapped
 * between domain and persistence models.
 */
final class CoffeeOrderMappingException extends RuntimeException {

    /**
     * Creates a new mapping exception.
     *
     * @param message exception message
     * @param cause underlying cause
     */
    CoffeeOrderMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
