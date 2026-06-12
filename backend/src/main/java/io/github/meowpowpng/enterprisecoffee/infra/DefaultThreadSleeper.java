package io.github.meowpowpng.enterprisecoffee.infra;

import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;

import java.time.Duration;
import java.util.Objects;

/**
 * Default {@link ThreadSleeper} implementation.
 */
public final class DefaultThreadSleeper implements ThreadSleeper {

    private final Duration interval;

    /**
     * Creates a new thread sleeper.
     *
     * @param interval sleep interval
     *
     * @throws NullPointerException if {@code interval} is {@code null}
     * @throws IllegalArgumentException if {@code interval} is not positive
     */
    public DefaultThreadSleeper(Duration interval) {
        Objects.requireNonNull(interval, "interval must not be null");
        if (!interval.isPositive()) {
            throw new IllegalArgumentException("interval must be positive");
        }
        this.interval = interval;
    }

    @Override
    public void sleep() {
        try {
            Thread.sleep(interval);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread was interrupted while sleeping", e);
        }
    }
}
