package io.github.meowpowpng.enterprisecoffee.internal.brew;

import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

/**
 * JPA-backed coffee-brewing job repository.
 */
@Repository
public class JpaCoffeeBrewJobRepository implements CoffeeBrewJobRepository {

    private final JpaCoffeeBrewJobCrudRepository repository;

    JpaCoffeeBrewJobRepository(JpaCoffeeBrewJobCrudRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    @Override
    public CoffeeBrewJob create(CoffeeBrewJob job) {
        Objects.requireNonNull(job, "job");

        try {
            var entity = CoffeeBrewJobMapper.toEntity(job);
            var persisted = repository.save(entity);

            return CoffeeBrewJobMapper.toDomain(persisted);
        }
        catch (CoffeeBrewJobMappingException e) {
            throw e;
        }
        catch (Exception e) {
            var message = "failed to create coffee brewing job";
            throw new CoffeeBrewJobPersistenceException(message, e);
        }
    }

    @Override
    public CoffeeBrewJob update(CoffeeBrewJob job) {
        Objects.requireNonNull(job, "job");

        var updated = repository.update(
                job.id().value(),
                job.status(),
                job.progress()
        );
        if (updated == 0) {
            var message = "coffee brewing job not found: " + job.id().value();
            throw new CoffeeBrewJobPersistenceException(message);
        }
        return job;
    }

    @Override
    public Optional<CoffeeBrewJob> findById(CoffeeBrewJob.Identifier id) {
        Objects.requireNonNull(id, "id");

        try {
            return repository.findById(id.value())
                    .map(CoffeeBrewJobMapper::toDomain);
        }
        catch (CoffeeBrewJobMappingException e) {
            throw e;
        }
        catch (RuntimeException e) {
            var message = "failed to find coffee brewing job";
            throw new CoffeeBrewJobPersistenceException(message, e);
        }
    }
}
