package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * JPA-backed coffee job repository.
 */
@Repository
public class JpaCoffeeJobRepository implements CoffeeJobRepository {

    private final JpaCoffeeJobCrudRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    JpaCoffeeJobRepository(JpaCoffeeJobCrudRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    /**
     * <strong>Implementation Note:</strong>
     * Marked as transactional because JPA persist
     * and flush operations require an active transaction.
     */
    @Override
    @Transactional
    public void create(CoffeeJob job) {
        Objects.requireNonNull(job, "job");
        try {
            var entity = CoffeeJobMapper.toEntity(job);

            entityManager.persist(entity);
            entityManager.flush();
        }
        catch (CoffeeJobMappingException e) {
            throw e;
        }
        catch (EntityExistsException e) {
            var message = "coffee job already exists: " + job.id().value();
            throw new CoffeeJobPersistenceException(message, e);
        }
        catch (RuntimeException e) {
            var message = "failed to create coffee job";
            throw new CoffeeJobPersistenceException(message, e);
        }
    }

    /**
     * <strong>Implementation Note:</strong>
     * Marked as transactional because JPQL update
     * queries require an active transaction.
     */
    @Override
    @Transactional
    public void update(CoffeeJob job) {
        Objects.requireNonNull(job, "job");

        var updated = repository.update(
                job.id().value(),
                job.status(),
                job.progress().value()
        );
        if (updated == 0) {
            var message = "coffee job not found: " + job.id().value();
            throw new CoffeeJobPersistenceException(message);
        }
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
            var message = "failed to find coffee job";
            throw new CoffeeJobPersistenceException(message, e);
        }
    }

    /**
     * <strong>Implementation Note:</strong>
     * Marked as transactional because delete
     * queries require an active transaction.
     */
    @Override
    @Transactional
    public long deleteNotUpdatedSince(Instant cutoff) {
        Objects.requireNonNull(cutoff, "cutoff");
        try {
            return repository.deleteByUpdatedAtBefore(cutoff);
        }
        catch (RuntimeException e) {
            var message = "failed to delete coffee jobs updated before: " + cutoff;
            throw new CoffeeJobPersistenceException(message, e);
        }
    }
}
