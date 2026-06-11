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
    public CoffeeBrewJob save(CoffeeBrewJob job) {
        Objects.requireNonNull(job, "job");

        var entity = repository.findById(job.id().value()).orElseGet(() ->
                CoffeeBrewJobMapper.toEntity(job)
        );
        entity.setStatus(job.status());
        entity.setProgress(job.progress());

        return CoffeeBrewJobMapper.toDomain(entity);
    }

    @Override
    public Optional<CoffeeBrewJob> findById(CoffeeBrewJob.Identifier id) {
        Objects.requireNonNull(id, "id");

        return repository
                .findById(id.value())
                .map(CoffeeBrewJobMapper::toDomain);
    }
}
