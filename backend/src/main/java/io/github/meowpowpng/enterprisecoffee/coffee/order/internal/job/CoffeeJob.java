package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;

import java.util.Objects;
import java.util.UUID;

/**
 * Coffee brewing job managed by backend.
 * <p>
 * <strong>Guarantees:</strong>
 * <ul>
 *   <li>Job progress never decreases.</li>
 *   <li>A completed job always has 100% progress.</li>
 * </ul>
 */
public final class CoffeeJob {

    private final Id id;
    private final CoffeeOrder.Id orderId;

    private Status status;
    private Progress progress;

    /**
     * Creates a new coffee job.
     * <p>
     * <strong>API Notes:</strong>
     * The job initially starts in {@link Status#PENDING} state.
     *
     * @param id unique identifier of the job
     * @param orderId unique identifier of the coffee order
     * @param status current status of the job
     *
     * @throws NullPointerException if {@code id} or {@code status} is null
     */
    private CoffeeJob(Id id, CoffeeOrder.Id orderId, Status status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.progress = Progress.initial();
    }

    /**
     * Creates a new coffee job.
     *
     * @param orderId unique identifier of the coffee order
     */
    public static CoffeeJob create(CoffeeOrder.Id orderId) {
        return new CoffeeJob(CoffeeJob.Id.generate(), orderId, Status.PENDING);
    }

    /**
     * Restores an existing coffee job.
     *
     * @param id unique identifier of the job
     * @param orderId unique identifier of the coffee order
     * @param status current status of the job
     * @param progress current progress of the job
     *
     * @throws NullPointerException if {@code id} or {@code status} is null
     * @throws IllegalArgumentException if {@code progress}
     * is outside valid range {@code 0-100}
     */
    static CoffeeJob restore(Id id, CoffeeOrder.Id orderId, Status status, int progress) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(status, "status must not be null");

        var job = new CoffeeJob(id, orderId, status);
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
     * Marks the job as completed
     * and updates progress to 100%.
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
     * Unique identifier of a coffee job.
     */
    public record Id(UUID value) {

        /**
         * Creates a new job identifier.
         *
         * @param value identifier value
         *
         * @throws NullPointerException if {@code value} is null
         */
        public Id {
            Objects.requireNonNull(value, "value must not be null");
        }

        /**
         * Generates a new unique coffee job identifier.
         */
        public static Id generate() {
            return new Id(UUID.randomUUID());
        }
    }

    /**
     * Current status of a coffee job.
     */
    public enum Status {

        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoffeeJob that)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(orderId, that.orderId)
                && status == that.status
                && Objects.equals(progress, that.progress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, status, progress);
    }
}
