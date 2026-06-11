package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event;

import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewJob;

import java.util.Objects;

public record CoffeeBrewJobProgressUpdatedEvent(
        CoffeeBrewJob job,
        int previousProgress
) implements DomainEvent {

    public CoffeeBrewJobProgressUpdatedEvent {
        Objects.requireNonNull(job, "job must not be null");
        if (previousProgress < 0 || previousProgress > 100) {
            throw new IllegalArgumentException("previousProgress must be between 0 and 100");
        }
    }
}
