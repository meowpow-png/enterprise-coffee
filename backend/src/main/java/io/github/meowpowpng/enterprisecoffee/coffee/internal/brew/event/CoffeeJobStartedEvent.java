package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeJob;
import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

public record CoffeeJobStartedEvent(CoffeeJob job) implements DomainEvent {

    public CoffeeJobStartedEvent {
        Objects.requireNonNull(job, "job must not be null");
    }
}

