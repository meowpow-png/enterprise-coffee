package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;

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
     * @throws CoffeeMachineProtocolException if the coffee machine returns an invalid response
     * @throws IllegalStateException if the machine client returned an unexpected result
     */
    CoffeeOrderResponse order(CoffeeOrderRequest request);

    /**
     * Returns the latest coffee orders.
     *
     * @param limit maximum number of coffee orders to retrieve
     *
     * @return latest coffee orders in descending creation order
     * @throws IllegalArgumentException if {@code limit} is less than {@code 1}
     */
    CoffeeOrdersResponse findLatest(int limit);
}
