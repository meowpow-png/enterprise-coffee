package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

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
     * @return the created coffee job
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws CoffeeJobMappingException if the job cannot be mapped
     * @throws CoffeeJobPersistenceException if the job cannot be persisted
     */
    CoffeeJob create(CoffeeJob job);

    /**
     * Updates an existing coffee job.
     *
     * @param job coffee job to update
     *
     * @return the updated coffee job
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws CoffeeJobPersistenceException if the job does not exist
     */
    CoffeeJob update(CoffeeJob job);

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
}
