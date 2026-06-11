package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewJobRepository;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
class CoffeeBrewJobEventHandler {

    private final CoffeeBrewJobRepository repository;

    CoffeeBrewJobEventHandler(CoffeeBrewJobRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    @Async
    @Transactional
    @EventListener
    public void onStarted(CoffeeBrewJobStartedEvent event) {
        repository.create(event.job());
    }

    @Async
    @Transactional
    @EventListener
    public void onProgressUpdated(CoffeeBrewJobProgressUpdatedEvent event) {
        repository.update(event.job());
    }

    @Async
    @Transactional
    @EventListener
    public void onFinished(CoffeeBrewJobFinishedEvent event) {
        repository.update(event.job());
    }
}
