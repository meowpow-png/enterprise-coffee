package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data repository for coffee job entities.
 */
@SuppressWarnings("unused")
public interface JpaCoffeeJobCrudRepository extends JpaRepository<CoffeeJobEntity, UUID> {

    //@formatter:off
    @Modifying
    @Query("""
        UPDATE CoffeeJobEntity job
        SET
            job.status = :status,
            job.progress = :progress,
            job.updatedAt = CURRENT_TIMESTAMP
        WHERE job.id = :id
        """)
    int update(
            UUID id,
            CoffeeJob.Status status,
            int progress
    );
    //@formatter:on

    /**
     * Deletes coffee jobs that have not
     * been updated since the specified instant.
     *
     * @param cutoff jobs last updated before this instant are deleted
     *
     * @return the number of deleted coffee jobs
     * @throws NullPointerException if {@code cutoff} is {@code null}
     * @throws CoffeeJobPersistenceException if the jobs cannot be deleted
     */
    long deleteByUpdatedAtBefore(Instant cutoff);
}
