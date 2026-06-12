package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for coffee order entities.
 */
@SuppressWarnings("unused")
interface JpaCoffeeOrderCrudRepository extends JpaRepository<CoffeeOrderEntity, UUID> {}
