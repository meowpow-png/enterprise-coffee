package io.github.meowpowpng.enterprisecoffee.coffee.internal.client;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Current brewing progress reported by the machine.
 */
public record MachineProgressResponse(@Nullable CoffeeType type, Progress progress) {

    /**
     * Creates a new machine progress response.
     *
     * @param type currently brewed coffee type, or {@code null} if the machine is idle
     * @param progress current brewing progress
     *
     * @throws NullPointerException if {@code progress} is {@code null}
     */
    public MachineProgressResponse {
        Objects.requireNonNull(progress, "progress must not be null");
    }
}
