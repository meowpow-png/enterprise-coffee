package io.github.meowpowpng.enterprisecoffee.internal.brew;

import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

public record CoffeeBrewJobChangedEvent(CoffeeBrewJob job) implements DomainEvent {

    public CoffeeBrewJobChangedEvent {
        Objects.requireNonNull(job, "job must not be null");
    }

    public static CoffeeBrewJobChangedEvent of(CoffeeBrewJob job) {
        return new CoffeeBrewJobChangedEvent(job);
    }
}
