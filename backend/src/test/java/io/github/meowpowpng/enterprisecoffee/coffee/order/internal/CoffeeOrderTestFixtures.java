package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeTestFixtures;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderRequest;

import java.util.UUID;

public final class CoffeeOrderTestFixtures {

    private CoffeeOrderTestFixtures() {}

    public static CoffeeOrder validCoffeeOrder() {
        return CoffeeOrder.restore(
                new CoffeeOrder.Id(UUID.randomUUID()),
                CoffeeTestFixtures.validCoffeeType(),
                CoffeeOrder.Status.PENDING
        );
    }

    public static CoffeeOrderRequest validCoffeeOrderRequest() {
        return new CoffeeOrderRequest("ESPRESSO");
    }
}
