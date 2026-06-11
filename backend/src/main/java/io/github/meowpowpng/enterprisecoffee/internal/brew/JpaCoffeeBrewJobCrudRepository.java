package io.github.meowpowpng.enterprisecoffee.internal.brew;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

/**
 * Spring Data repository for coffee-brewing job entities.
 */
@SuppressWarnings("unused")
interface JpaCoffeeBrewJobCrudRepository extends JpaRepository<CoffeeBrewJobEntity, UUID> {

    @Modifying
    @Query("""
        UPDATE CoffeeBrewJobEntity job
        SET
            job.status = :status,
            job.progress = :progress,
            job.updatedAt = CURRENT_TIMESTAMP
        WHERE job.id = :id
        """)
    int update(
            UUID id,
            CoffeeBrewJob.Status status,
            int progress
    );
}
