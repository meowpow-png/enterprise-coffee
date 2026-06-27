package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
final class TestCoffeeOrderService implements CoffeeOrderService {

    public static final String INVALID_ORDER_MESSAGE = "invalid order";
    public static final String PROCESSING_FAILURE_MESSAGE = "processing failure";

    private CoffeeOrderResponse response;
    private CoffeeOrdersResponse ordersResponse;
    private boolean invalidOrder;
    private boolean processingFails;
    private int limit;

    TestCoffeeOrderService() {
        this.response = new CoffeeOrderResponse("accepted");
        this.ordersResponse = new CoffeeOrdersResponse(List.of());
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

    @Override
    public CoffeeOrdersResponse findLatest(int limit) {
        this.limit = limit;
        return ordersResponse;
    }

    void response(CoffeeOrderResponse response) {
        this.response = response;
    }

    void ordersResponse(CoffeeOrdersResponse response) {
        this.ordersResponse = response;
    }

    void markInvalidOrder() {
        invalidOrder = true;
    }

    void markProcessingFailure() {
        processingFails = true;
    }

    int limit() {
        return limit;
    }

    void reset() {
        response = new CoffeeOrderResponse("accepted");
        ordersResponse = new CoffeeOrdersResponse(List.of());
        invalidOrder = false;
        processingFails = false;
        limit = 0;
    }
}
