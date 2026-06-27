package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for coffee order entities.
 */
@SuppressWarnings("unused")
public interface JpaCoffeeOrderCrudRepository extends JpaRepository<CoffeeOrderEntity, UUID> {

    /**
     * Returns coffee orders from newest to oldest.
     *
     * @param pageable pagination information
     */
    List<CoffeeOrderEntity> findByOrderByCreatedAtDesc(Pageable pageable);
}
