package io.github.meowpowpng.enterprisecoffee.coffee.internal.client;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

import java.util.Objects;

/**
 * Current brewing progress reported by the machine.
 * <p>
 * <strong>Implementation Note:</strong>
 * {@code type} is intentionally a String rather then
 * {@link CoffeeType} because the machine explicitly
 * uses an empty string to represent an idle state.
 */
public record MachineProgressResponse(String type, int progress) {

    /**
     * Creates a new machine progress response.
     *
     * @param type currently brewed coffee type, or empty if the machine is idle
     * @param progress current brewing progress
     *
     * @throws NullPointerException if {@code type} is {@code null}
     */
    public MachineProgressResponse {
        Objects.requireNonNull(type, "type must not be null");
    }
}
