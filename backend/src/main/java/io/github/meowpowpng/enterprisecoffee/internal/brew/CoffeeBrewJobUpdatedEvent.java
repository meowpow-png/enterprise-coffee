package io.github.meowpowpng.enterprisecoffee.internal.brew;

import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

public record CoffeeBrewJobUpdatedEvent(CoffeeBrewJob job) implements DomainEvent {

    public CoffeeBrewJobUpdatedEvent {
        Objects.requireNonNull(job, "job must not be null");
    }

    public static CoffeeBrewJobUpdatedEvent of(CoffeeBrewJob job) {
        return new CoffeeBrewJobUpdatedEvent(job);
    }
}
