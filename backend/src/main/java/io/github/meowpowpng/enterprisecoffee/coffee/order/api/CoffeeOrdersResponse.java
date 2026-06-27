package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import java.util.List;
import java.util.Objects;

/**
 * Collection of coffee orders.
 */
public record CoffeeOrdersResponse(List<CoffeeOrderView> orders) {

    /**
     * Creates a new coffee orders response.
     *
     * @param orders coffee orders
     *
     * @throws NullPointerException if {@code orders} is {@code null}
     */
    public CoffeeOrdersResponse {
        Objects.requireNonNull(orders, "orders must not be null");
    }
}
