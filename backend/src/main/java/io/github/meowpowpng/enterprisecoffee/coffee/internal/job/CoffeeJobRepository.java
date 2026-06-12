package io.github.meowpowpng.enterprisecoffee.coffee.internal.job;

import java.util.Optional;

/**
 * Repository for coffee-brewing jobs.
 */
public interface CoffeeJobRepository {

    /**
     * Creates a new coffee-brewing job.
     *
     * @param job coffee-brewing job to create
     *
     * @return the created coffee-brewing job
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws CoffeeJobMappingException if the job cannot be mapped
     * @throws CoffeeJobPersistenceException if the job cannot be persisted
     */
    CoffeeJob create(CoffeeJob job);

    /**
     * Updates an existing coffee-brewing job.
     *
     * @param job coffee-brewing job to update
     *
     * @return the updated coffee brewing job
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws IllegalStateException if the job does not exist
     */
    CoffeeJob update(CoffeeJob job);

    /**
     * Returns the coffee-brewing job with the specified identifier.
     *
     * @param id identifier of the coffee-brewing job
     *
     * @return the coffee-brewing job if found; otherwise empty
     * @throws NullPointerException if {@code id} is {@code null}
     * @throws CoffeeJobMappingException if the job cannot be mapped
     * @throws CoffeeJobPersistenceException if the job cannot be retrieved
     */
    Optional<CoffeeJob> findById(CoffeeJob.Id id);
}
