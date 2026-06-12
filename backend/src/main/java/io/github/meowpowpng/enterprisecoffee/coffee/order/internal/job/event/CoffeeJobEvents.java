package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.event;

import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJob;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

/**
 * Events used to signal changes coffee job lifecycle.
 */
public final class CoffeeJobEvents {

    private CoffeeJobEvents() {}

    /**
     * Creates an event signaling that a coffee job has started.
     *
     * @param job started job
     */
    public static Started started(CoffeeJob job) {
        return new Started(job);
    }

    /**
     * Creates an event signaling that a
     * coffee job has reported progress.
     *
     * @param job updated job
     * @param previous previous progress value
     */
    public static ProgressUpdated progressUpdated(CoffeeJob job, Progress previous) {
        return new ProgressUpdated(job, previous);
    }

    /**
     * Creates an event signaling that a coffee job has finished.
     *
     * @param job finished job
     */
    public static Finished finished(CoffeeJob job) {
        return new Finished(job);
    }

    /**
     * Event that signals that a coffee job has started.
     */
    public record Started(CoffeeJob job) implements DomainEvent {

        public Started {
            Objects.requireNonNull(job, "job must not be null");
        }
    }

    /**
     * Event that signals that a
     * coffee job has reported progress.
     */
    public record ProgressUpdated(CoffeeJob job, Progress previous) implements DomainEvent {

        public ProgressUpdated {
            Objects.requireNonNull(job, "job must not be null");
        }
    }

    /**
     * Event that signals that a coffee job has finished.
     */
    public record Finished(CoffeeJob job) implements DomainEvent {

        public Finished {
            Objects.requireNonNull(job, "job must not be null");
        }
    }
}
