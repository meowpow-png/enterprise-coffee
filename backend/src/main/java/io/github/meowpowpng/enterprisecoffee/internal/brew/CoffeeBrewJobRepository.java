package io.github.meowpowpng.enterprisecoffee.internal.brew;

import java.util.Optional;

/**
 * Repository for coffee-brewing jobs.
 */
public interface CoffeeBrewJobRepository {

    /**
     * Saves the specified coffee brewing job.
     *
     * @param job coffee brewing job to save
     *
     * @return persisted coffee brewing job
     * @throws NullPointerException if {@code job} is {@code null}
     */
    CoffeeBrewJob save(CoffeeBrewJob job);

    /**
     * Returns the coffee brewing job with the specified identifier.
     *
     * @param id identifier of the coffee brewing job
     *
     * @return coffee brewing job if found; otherwise empty
     * @throws NullPointerException if {@code id} is {@code null}
     */
    Optional<CoffeeBrewJob> findById(CoffeeBrewJob.Identifier id);
}
