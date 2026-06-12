package io.github.meowpowpng.enterprisecoffee.coffee.model;

import java.util.Objects;

/**
 * Represents the coffee type.
 */
public record CoffeeType(String value) {

    /**
     * Creates a coffee type.
     *
     * @param value coffee type name
     *
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is blank
     */
    public CoffeeType {
        Objects.requireNonNull(value, "value must not be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }
}
