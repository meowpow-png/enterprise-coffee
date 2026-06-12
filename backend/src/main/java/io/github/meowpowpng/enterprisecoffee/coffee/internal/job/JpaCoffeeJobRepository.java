package io.github.meowpowpng.enterprisecoffee.coffee.internal.job;

import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

/**
 * JPA-backed coffee-brewing job repository.
 */
@Repository
public class JpaCoffeeJobRepository implements CoffeeJobRepository {

    private final JpaCoffeeJobCrudRepository repository;

    JpaCoffeeJobRepository(JpaCoffeeJobCrudRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    @Override
    public CoffeeJob create(CoffeeJob job) {
        Objects.requireNonNull(job, "job");

        try {
            var entity = CoffeeJobMapper.toEntity(job);
            var persisted = repository.save(entity);

            return CoffeeJobMapper.toDomain(persisted);
        }
        catch (CoffeeJobMappingException e) {
            throw e;
        }
        catch (RuntimeException e) {
            var message = "failed to create coffee brewing job";
            throw new CoffeeJobPersistenceException(message, e);
        }
    }

    @Override
    public CoffeeJob update(CoffeeJob job) {
        Objects.requireNonNull(job, "job");

        var updated = repository.update(
                job.id().value(),
                job.status(),
                job.progress().value()
        );
        if (updated == 0) {
            var message = "coffee brewing job not found: " + job.id().value();
            throw new CoffeeJobPersistenceException(message);
        }
        return job;
    }

    @Override
    public Optional<CoffeeJob> findById(CoffeeJob.Id id) {
        Objects.requireNonNull(id, "id");

        try {
            return repository.findById(id.value())
                    .map(CoffeeJobMapper::toDomain);
        }
        catch (CoffeeJobMappingException e) {
            throw e;
        }
        catch (RuntimeException e) {
            var message = "failed to find coffee brewing job";
            throw new CoffeeJobPersistenceException(message, e);
        }
    }
}
