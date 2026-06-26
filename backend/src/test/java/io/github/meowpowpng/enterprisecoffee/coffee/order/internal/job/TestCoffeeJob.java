package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;

import java.util.UUID;

public final class TestCoffeeJob {

    private TestCoffeeJob() {}

    public static CoffeeJob validCoffeeJob() {
        return CoffeeJob.create(
                new CoffeeOrder.Id(UUID.randomUUID())
        );
    }
}
