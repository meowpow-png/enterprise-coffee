package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Current coffee brewing
 * progress reported by the machine.
 */
public record MachineCoffeeProgress(@Nullable CoffeeType type, Progress progress) {

    /**
     * Creates new machine coffee brewing progress.
     *
     * @param type currently brewed coffee type, or {@code null}
     * @param progress current brewing progress
     *
     * @throws NullPointerException if {@code progress} is {@code null}
     */
    public MachineCoffeeProgress {
        Objects.requireNonNull(progress, "progress must not be null");
    }
}
