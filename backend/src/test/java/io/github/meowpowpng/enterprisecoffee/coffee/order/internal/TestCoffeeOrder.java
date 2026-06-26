package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeTestFixtures;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderRequest;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import java.util.UUID;

public final class TestCoffeeOrder {

    private static final TestClock CLOCK = TestClock.create();
    private static final CoffeeOrderFactory FACTORY = new CoffeeOrderFactory(CLOCK);

    private TestCoffeeOrder() {}

    public static CoffeeOrder validCoffeeOrder() {
        return CoffeeOrder.restore(
                new CoffeeOrder.Id(UUID.randomUUID()),
                CoffeeTestFixtures.validCoffeeType(),
                CoffeeOrder.Status.PENDING,
                CLOCK.instant()
        );
    }

    public static CoffeeOrderRequest validCoffeeOrderRequest() {
        return new CoffeeOrderRequest("ESPRESSO");
    }

    public static CoffeeOrder orderOfType(CoffeeType type) {
        return FACTORY.create(type);
    }
}
