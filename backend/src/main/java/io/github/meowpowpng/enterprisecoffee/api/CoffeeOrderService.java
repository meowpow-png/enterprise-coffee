package io.github.meowpowpng.enterprisecoffee.api;

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
     */
    ClientOrderResponse order(ClientOrderRequest request);
}
