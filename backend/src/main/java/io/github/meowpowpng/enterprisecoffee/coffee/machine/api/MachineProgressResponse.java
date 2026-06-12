package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import java.util.Objects;

/**
 * Current coffee brewing
 * progress reported by the machine.
 */
record MachineProgressResponse(String type, int progress) {

    /**
     * Creates a new machine progress response.
     *
     * @param type currently brewed coffee type (allowed empty)
     * @param progress current brewing progress
     *
     * @throws NullPointerException if {@code type} is {@code null}
     */
    MachineProgressResponse {
        Objects.requireNonNull(type, "type must not be null");
    }
}
