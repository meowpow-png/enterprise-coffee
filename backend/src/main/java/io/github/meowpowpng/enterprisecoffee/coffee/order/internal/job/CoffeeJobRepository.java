package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import java.time.Instant;
import java.util.Optional;

/**
 * Repository for coffee jobs.
 */
public interface CoffeeJobRepository {

    /**
     * Creates a new coffee job.
     *
     * @param job coffee job to create
     *
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws CoffeeJobMappingException if the job cannot be mapped
     * @throws CoffeeJobPersistenceException if the job already
     * exists or cannot be persisted
     */
    void create(CoffeeJob job);

    /**
     * Updates an existing coffee job.
     *
     * @param job coffee job to update
     *
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws CoffeeJobPersistenceException if the job does not exist
     */
    void update(CoffeeJob job);

    /**
     * Returns the coffee job with the specified identifier.
     *
     * @param id identifier of the coffee job
     *
     * @return the coffee job if found; otherwise empty
     * @throws NullPointerException if {@code id} is {@code null}
     * @throws CoffeeJobMappingException if the job cannot be mapped
     * @throws CoffeeJobPersistenceException if the job cannot be retrieved
     */
    Optional<CoffeeJob> findById(CoffeeJob.Id id);

    /**
     * Deletes coffee jobs that have not
     * been updated since the specified instant.
     *
     * @param cutoff jobs last updated before this instant are deleted
     *
     * @return the number of deleted coffee jobs
     * @throws NullPointerException if {@code cutoff} is {@code null}
     * @throws CoffeeJobPersistenceException if the jobs cannot be deleted
     */
    long deleteNotUpdatedSince(Instant cutoff);
}
