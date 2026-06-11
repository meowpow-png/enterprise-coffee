package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewJobRepository;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
class CoffeeBrewJobEventHandler {

    private static final CoffeeBrewJobEventHandlerLogger log = new CoffeeBrewJobEventHandlerLogger();

    private final CoffeeBrewJobRepository repository;

    CoffeeBrewJobEventHandler(CoffeeBrewJobRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    @Async
    @Transactional
    @EventListener
    public void onStarted(CoffeeBrewJobStartedEvent event) {
        var id = event.job().id().value();
        log.started(id);
        try {
            repository.create(event.job());
        }
        catch (Exception e) {
            log.persistenceFailed(id, e);
        }
    }

    @Async
    @Transactional
    @EventListener
    public void onProgressUpdated(CoffeeBrewJobProgressUpdatedEvent event) {
        var id = event.job().id().value();
        log.progressUpdated(id, event.previousProgress(), event.job().progress().value());
        try {
            repository.update(event.job());
        }
        catch (Exception e) {
            log.persistenceFailed(id, e);
        }
    }

    @Async
    @Transactional
    @EventListener
    public void onFinished(CoffeeBrewJobFinishedEvent event) {
        var id = event.job().id().value();
        log.finished(id, event.job().progress().value());
        try {
            repository.update(event.job());
        }
        catch (Exception e) {
            log.persistenceFailed(id, e);
        }
    }
}
