package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.TestCoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import java.time.Instant;

public final class TestCoffeeOrder {

    private static final TestClock CLOCK = TestClock.create();
    private static final CoffeeOrderFactory FACTORY = new CoffeeOrderFactory(CLOCK);

    private TestCoffeeOrder() {}

    public static CoffeeOrder create(String type, Instant createdAt) {
        return CoffeeOrder.create(new CoffeeType(type), createdAt);
    }

    public static CoffeeOrder create(CoffeeType type) {
        return FACTORY.create(type);
    }

    public static CoffeeOrder create(String type) {
        return FACTORY.create(new CoffeeType(type));
    }

    public static CoffeeOrder create() {
        return create(TestCoffeeType.create());
    }
}
