package io.github.meowpowpng.enterprisecoffee.internal.brew;

import java.util.Objects;
import java.util.UUID;

/**
 * Coffee brewing job managed by coffee backend.
 */
public final class CoffeeBrewJob {

    private final Identifier id;

    private Status status;
    private int progress;

    /**
     * Creates a new coffee-brewing job.
     *
     * @param id unique identifier of the brewing job
     * @param status current status of the brewing job
     *
     * @throws NullPointerException if {@code id} or {@code status} is null
     */
    private CoffeeBrewJob(Identifier id, Status status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Creates a new coffee-brewing job.
     */
    public static CoffeeBrewJob create() {
        return new CoffeeBrewJob(Identifier.generate(), Status.PENDING);
    }

    /**
     * Restores an existing coffee-brewing job.
     *
     * @param id unique identifier of the brewing job
     * @param status current status of the brewing job
     * @param progress current progress of the brewing job
     *
     * @throws NullPointerException if {@code id} or {@code status} is null
     * @throws IllegalArgumentException if {@code progress}
     * is outside valid range {@code 0-100}
     */
    static CoffeeBrewJob restore(Identifier id, Status status, int progress) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(status, "status must not be null");

        if (progress < 0 || progress > 100) {
            var message = "progress must be between 0 and 100 but was " + progress;
            throw new IllegalArgumentException(message);
        }
        var job = new CoffeeBrewJob(id, status);
        job.progress = progress;

        return job;
    }

    /**
     * Marks the job as started.
     *
     * @throws IllegalStateException if the job is not pending
     */
    void start() {
        if (status != Status.PENDING) {
            var message = "expected status PENDING but was " + status;
            throw new IllegalStateException(message);
        }
        status = Status.IN_PROGRESS;
    }

    /**
     * Updates the current job progress.
     *
     * @param progress new progress value
     *
     * @throws IllegalStateException if the job is not in progress
     * @throws IllegalArgumentException if {@code progress}
     * is outside valid range {@code 0-100}
     */
    void updateProgress(int progress) {
        if (status != Status.IN_PROGRESS) {
            var message = "cannot update progress for job with status " + status;
            throw new IllegalStateException(message);
        }
        if (progress < 0 || progress > 100) {
            var message =  "progress must be between 0 and 100 but was " + progress;
            throw new IllegalArgumentException(message);
        }
        this.progress = progress;
    }

    /**
     * Marks the job as completed.
     *
     * @throws IllegalStateException if the job is not in progress
     */
    void complete() {
        if (status != Status.IN_PROGRESS) {
            var message =  "cannot complete job with status " + status;
            throw new IllegalStateException(message);
        }
        progress = 100;
        status = Status.COMPLETED;
    }

    /**
     * Marks the job as failed.
     *
     * @throws IllegalStateException if the job has already completed or failed
     */
    void fail() {
        if (status == Status.COMPLETED || status == Status.FAILED) {
            var message = "cannot fail job with status " + status;
            throw new IllegalStateException(message);
        }
        status = Status.FAILED;
    }

    /**
     * Returns the job identifier.
     */
    Identifier id() {
        return id;
    }

    /**
     * Returns the current job status.
     */
    public Status status() {
        return status;
    }

    /**
     * Returns the current job progress.
     */
    int progress() {
        return progress;
    }

    /**
     * Unique identifier of a coffee-brewing job.
     */
    public record Identifier(UUID value) {

        /**
         * Creates a new brewing job identifier.
         *
         * @param value identifier value
         *
         * @throws NullPointerException if {@code value} is null
         */
        public Identifier {
            Objects.requireNonNull(value, "value must not be null");
        }

        /**
         * Generates a new unique coffee-brewing job identifier.
         */
        static Identifier generate() {
            return new Identifier(UUID.randomUUID());
        }
    }

    /**
     * Current status of a coffee brewing job.
     */
    public enum Status {

        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
