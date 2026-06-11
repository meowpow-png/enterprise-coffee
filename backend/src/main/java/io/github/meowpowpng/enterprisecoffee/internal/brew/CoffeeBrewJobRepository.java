package io.github.meowpowpng.enterprisecoffee.internal.brew;

import java.util.Optional;

/**
 * Repository for coffee-brewing jobs.
 */
public interface CoffeeBrewJobRepository {

    /**
     * Creates a new coffee-brewing job.
     *
     * @param job coffee brewing job to create
     *
     * @return the created coffee-brewing job
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws IllegalStateException if a job with the same identifier
     * already exists
     */
    CoffeeBrewJob create(CoffeeBrewJob job);

    /**
     * Updates an existing coffee-brewing job.
     *
     * @param job coffee brewing job to update
     *
     * @return the updated coffee brewing job
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws IllegalStateException if the job does not exist
     */
    CoffeeBrewJob update(CoffeeBrewJob job);

    /**
     * Returns the coffee brewing job with the specified identifier.
     *
     * @param id identifier of the coffee brewing job
     *
     * @return the coffee brewing job if found; otherwise empty
     * @throws NullPointerException if {@code id} is {@code null}
     */
    Optional<CoffeeBrewJob> findById(CoffeeBrewJob.Identifier id);
}
