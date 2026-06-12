package io.github.meowpowpng.enterprisecoffee.coffee.api;

import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderProcessingException;

/**
 * Manages coffee order operations.
 */
public interface CoffeeOrderService {

    /**
     * Places a coffee order according to the specified request.
     *
     * @param request coffee order request
     *
     * @return order processing result
     * @throws NullPointerException if {@code request} is {@code null}
     * @throws CoffeeOrderInvalidException if the request cannot be
     * accepted because the specified coffee order is invalid
     * @throws CoffeeOrderProcessingException if the order cannot be processed
     * @throws IllegalStateException if the machine client returned an unexpected result
     */
    ClientOrderResponse order(ClientOrderRequest request);
}
