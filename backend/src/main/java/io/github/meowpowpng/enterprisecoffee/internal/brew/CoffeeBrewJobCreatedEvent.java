package io.github.meowpowpng.enterprisecoffee.internal.brew;

import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

public record CoffeeBrewJobCreatedEvent(CoffeeBrewJob job) implements DomainEvent {

    public CoffeeBrewJobCreatedEvent {
        Objects.requireNonNull(job, "job must not be null");
    }

    public static CoffeeBrewJobUpdatedEvent of(CoffeeBrewJob job) {
        return new CoffeeBrewJobUpdatedEvent(job);
    }
}

