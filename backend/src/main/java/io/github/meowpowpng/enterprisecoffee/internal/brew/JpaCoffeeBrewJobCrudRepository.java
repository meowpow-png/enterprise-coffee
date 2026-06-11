package io.github.meowpowpng.enterprisecoffee.internal.brew;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for coffee-brewing job entities.
 */
@SuppressWarnings("unused")
interface JpaCoffeeBrewJobCrudRepository extends JpaRepository<CoffeeBrewJobEntity, UUID> {}
