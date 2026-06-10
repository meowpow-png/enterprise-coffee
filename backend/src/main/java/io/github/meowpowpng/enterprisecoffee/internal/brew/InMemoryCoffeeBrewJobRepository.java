package io.github.meowpowpng.enterprisecoffee.internal.brew;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@link CoffeeBrewJobRepository} implementation.
 */
@Repository
class InMemoryCoffeeBrewJobRepository implements CoffeeBrewJobRepository {

    private final Map<CoffeeBrewJob.Identifier, CoffeeBrewJob> jobs = new ConcurrentHashMap<>();

    @Override
    public CoffeeBrewJob save(CoffeeBrewJob job) {
        Objects.requireNonNull(job, "job must not be null");

        jobs.put(job.id(), job);

        return job;
    }

    @Override
    public Optional<CoffeeBrewJob> findById(CoffeeBrewJob.Identifier id) {
        Objects.requireNonNull(id, "id must not be null");

        return Optional.ofNullable(jobs.get(id));
    }
}
