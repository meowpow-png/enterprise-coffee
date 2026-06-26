package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderRequest;

public class TestCoffeeOrderRequest {

    private TestCoffeeOrderRequest() {}

    public static CoffeeOrderRequest create() {
        return new CoffeeOrderRequest("ESPRESSO");
    }
}
