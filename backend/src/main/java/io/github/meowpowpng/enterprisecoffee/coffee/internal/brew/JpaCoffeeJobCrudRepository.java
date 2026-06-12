package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

/**
 * Spring Data repository for coffee-brewing job entities.
 */
@SuppressWarnings("unused")
interface JpaCoffeeJobCrudRepository extends JpaRepository<CoffeeJobEntity, UUID> {

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
}
