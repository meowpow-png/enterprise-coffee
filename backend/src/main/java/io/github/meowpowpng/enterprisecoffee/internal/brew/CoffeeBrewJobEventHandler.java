package io.github.meowpowpng.enterprisecoffee.internal.brew;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class CoffeeBrewJobEventHandler {

    private final CoffeeBrewJobRepository repository;

    CoffeeBrewJobEventHandler(CoffeeBrewJobRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    @Async
    @EventListener
    public void onEvent(CoffeeBrewJobCreatedEvent event) {
        repository.create(event.job());
    }

    @Async
    @EventListener
    public void onEvent(CoffeeBrewJobUpdatedEvent event) {
        repository.update(event.job());
    }
}
