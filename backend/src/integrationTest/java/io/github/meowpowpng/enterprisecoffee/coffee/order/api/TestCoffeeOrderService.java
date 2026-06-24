package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;

import org.jspecify.annotations.NullMarked;

@NullMarked
final class TestCoffeeOrderService implements CoffeeOrderService {

    public static final String INVALID_ORDER_MESSAGE = "invalid order";
    public static final String PROCESSING_FAILURE_MESSAGE = "processing failure";

    private CoffeeOrderResponse response;
    private boolean invalidOrder;
    private boolean processingFails;

    TestCoffeeOrderService() {
        this.response = new CoffeeOrderResponse("accepted");
    }

    @Override
    public CoffeeOrderResponse order(CoffeeOrderRequest request) {
        if (invalidOrder) {
            throw new CoffeeOrderInvalidException(INVALID_ORDER_MESSAGE);
        }
        if (processingFails) {
            throw new CoffeeOrderProcessingException(PROCESSING_FAILURE_MESSAGE);
        }
        return response;
    }

    void response(CoffeeOrderResponse response) {
        this.response = response;
    }

    void markInvalidOrder() {
        invalidOrder = true;
    }

    void markProcessingFailure() {
        processingFails = true;
    }

    void reset() {
        response = new CoffeeOrderResponse("accepted");
        invalidOrder = false;
        processingFails = false;
    }
}
