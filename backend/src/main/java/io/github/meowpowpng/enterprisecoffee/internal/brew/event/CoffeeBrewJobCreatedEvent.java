package io.github.meowpowpng.enterprisecoffee.internal.brew.event;

import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;
import io.github.meowpowpng.enterprisecoffee.internal.brew.CoffeeBrewJob;

import java.util.Objects;

public record CoffeeBrewJobCreatedEvent(CoffeeBrewJob job) implements DomainEvent {

    public CoffeeBrewJobCreatedEvent {
        Objects.requireNonNull(job, "job must not be null");
    }
}

