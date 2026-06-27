package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import java.util.List;
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
    Optional<CoffeeOrder> findById(CoffeeOrder.Id id);

    /**
     * Finds the latest coffee orders.
     *
     * @param limit maximum number of coffee orders to retrieve
     *
     * @return latest coffee orders from newest to oldest
     * @throws IllegalArgumentException if {@code limit} is less than {@code 1}
     * @throws CoffeeOrderPersistenceException if the orders cannot be retrieved
     */
    List<CoffeeOrder> findLatest(int limit);
}
