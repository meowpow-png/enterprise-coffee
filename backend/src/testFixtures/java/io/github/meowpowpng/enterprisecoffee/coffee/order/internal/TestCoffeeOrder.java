package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.TestCoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderRequest;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

public final class TestCoffeeOrder {

    private static final TestClock CLOCK = TestClock.create();
    private static final CoffeeOrderFactory FACTORY = new CoffeeOrderFactory(CLOCK);

    private TestCoffeeOrder() {}

    public static CoffeeOrder createOrder(CoffeeType type) {
        return FACTORY.create(type);
    }

    public static CoffeeOrder createOrder(String type) {
        return FACTORY.create(new CoffeeType(type));
    }

    public static CoffeeOrder createOrder() {
        return createOrder(TestCoffeeType.create());
    }

    public static CoffeeOrderRequest createRequest() {
        return new CoffeeOrderRequest("ESPRESSO");
    }
}
