package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.order.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import java.util.Objects;
import java.util.UUID;

/**
 * Coffee brewing job managed by coffee backend.
 */
public final class CoffeeBrewJob {

    private final Id id;
    private final CoffeeOrder.Id orderId;

    private Status status;
    private Progress progress;

    /**
     * Creates a new coffee-brewing job.
     *
     * @param id unique identifier of the brewing job
     * @param orderId unique identifier of the coffee order
     * @param status current status of the brewing job
     *
     * @throws NullPointerException if {@code id} or {@code status} is null
     */
    private CoffeeBrewJob(Id id, CoffeeOrder.Id orderId, Status status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.progress = Progress.initial();
    }

    /**
     * Creates a new coffee-brewing job.
     *
     * @param orderId unique identifier of the coffee order
     */
    public static CoffeeBrewJob create(CoffeeOrder.Id orderId) {
        return new CoffeeBrewJob(CoffeeBrewJob.Id.generate(), orderId, Status.PENDING);
    }

    /**
     * Restores an existing coffee-brewing job.
     *
     * @param id unique identifier of the brewing job
     * @param orderId unique identifier of the coffee order
     * @param status current status of the brewing job
     * @param progress current progress of the brewing job
     *
     * @throws NullPointerException if {@code id} or {@code status} is null
     * @throws IllegalArgumentException if {@code progress}
     * is outside valid range {@code 0-100}
     */
    static CoffeeBrewJob restore(Id id, CoffeeOrder.Id orderId, Status status, int progress) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(status, "status must not be null");

        var job = new CoffeeBrewJob(id, orderId, status);
        job.progress = Progress.of(progress);

        return job;
    }

    /**
     * Marks the job as started.
     *
     * @throws IllegalStateException if the job is not pending
     */
    public void start() {
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
     * @throws IllegalArgumentException if {@code progress} decreases
     */
    public void updateProgress(Progress progress) {
        if (status != Status.IN_PROGRESS) {
            var message = "cannot update progress for job with status " + status;
            throw new IllegalStateException(message);
        }
        if (progress.value() < this.progress.value()) {
            throw new IllegalArgumentException("progress cannot decrease");
        }
        this.progress = progress;
    }

    /**
     * Marks the job as completed.
     *
     * @throws IllegalStateException if the job is not in progress
     */
    public void complete() {
        if (status != Status.IN_PROGRESS) {
            var message = "cannot complete job with status " + status;
            throw new IllegalStateException(message);
        }
        progress = Progress.of(100);
        status = Status.COMPLETED;
    }

    /**
     * Marks the job as failed.
     *
     * @throws IllegalStateException if the job has already completed or failed
     */
    public void fail() {
        if (status == Status.COMPLETED || status == Status.FAILED) {
            var message = "cannot fail job with status " + status;
            throw new IllegalStateException(message);
        }
        status = Status.FAILED;
    }

    /**
     * Returns the job identifier.
     */
    public Id id() {
        return id;
    }

    /**
     * Returns the coffee order id this job belongs to.
     */
    public CoffeeOrder.Id orderId() {
        return orderId;
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
    public Progress progress() {
        return progress;
    }

    /**
     * Unique identifier of a coffee-brewing job.
     */
    public record Id(UUID value) {

        /**
         * Creates a new brewing job identifier.
         *
         * @param value identifier value
         *
         * @throws NullPointerException if {@code value} is null
         */
        public Id {
            Objects.requireNonNull(value, "value must not be null");
        }

        /**
         * Generates a new unique coffee-brewing job identifier.
         */
        static Id generate() {
            return new Id(UUID.randomUUID());
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
