package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

import java.util.Optional;

/**
 * Repository for managing coffee orders.
 */
public interface CoffeeOrderRepository {

    /**
     * Saves the specified coffee order.
     *
     * @param order coffee order to save
     *
     * @throws NullPointerException if {@code order} is {@code null}
     * @throws CoffeeOrderPersistenceException if the order cannot be persisted
     */
    void save(CoffeeOrder order);

    /**
     * Finds a coffee order by identifier.
     *
     * @param id coffee order identifier
     *
     * @return coffee order if found; otherwise an empty result
     * @throws NullPointerException if {@code id} is {@code null}
     * @throws CoffeeOrderPersistenceException if the order cannot be retrieved
     */
    Optional<CoffeeOrder> findById(CoffeeOrder.Identifier id);
}
