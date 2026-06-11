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

        var entity = CoffeeBrewJobMapper.toEntity(job);
        var persisted = repository.save(entity);

        return CoffeeBrewJobMapper.toDomain(persisted);
    }

    @Override
    public CoffeeBrewJob update(CoffeeBrewJob job) {
        Objects.requireNonNull(job, "job");

        var entity = repository.findById(job.id().value()).orElseThrow(() -> {
            var message = "coffee brewing job not found: %s";
            return new IllegalStateException(message.formatted(job.id().value()));
        });
        entity.setStatus(job.status());
        entity.setProgress(job.progress());

        var persisted = repository.save(entity);

        return CoffeeBrewJobMapper.toDomain(persisted);
    }

    @Override
    public Optional<CoffeeBrewJob> findById(CoffeeBrewJob.Identifier id) {
        Objects.requireNonNull(id, "id");

        return repository
                .findById(id.value())
                .map(CoffeeBrewJobMapper::toDomain);
    }
}
