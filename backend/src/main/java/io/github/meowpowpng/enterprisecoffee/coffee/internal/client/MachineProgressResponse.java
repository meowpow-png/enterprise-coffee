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
     * @param type coffee type currently brewed, or empty if the machine is idle
     * @param progress current brewing progress (0–100)
     *
     * @throws NullPointerException if {@code type} is {@code null}
     * @throws IllegalArgumentException if {@code progress}
     *         is outside the valid range {@code 0-100}
     */
    public MachineProgressResponse {
        Objects.requireNonNull(type, "type must not be null");

        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("progress must be in valid range (0-100)");
        }
    }
}
