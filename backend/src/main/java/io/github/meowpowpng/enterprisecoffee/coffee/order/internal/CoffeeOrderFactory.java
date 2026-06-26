package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Objects;

/**
 * Factory for creating coffee orders.
 */
@Component
final class CoffeeOrderFactory {

    private final Clock clock;

    CoffeeOrderFactory(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /**
     * Creates a new pending coffee order.
     *
     * @param type requested coffee type
     *
     * @throws NullPointerException if {@code type} is {@code null}
     */
    public CoffeeOrder create(CoffeeType type) {
        return CoffeeOrder.create(type, clock.instant());
    }
}
