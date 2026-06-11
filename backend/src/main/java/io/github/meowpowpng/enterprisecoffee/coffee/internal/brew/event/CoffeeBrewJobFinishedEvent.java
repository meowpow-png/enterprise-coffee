package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewJob;
import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

public record CoffeeBrewJobFinishedEvent(CoffeeBrewJob job) implements DomainEvent {

    public CoffeeBrewJobFinishedEvent {
        Objects.requireNonNull(job, "job must not be null");
    }
}
