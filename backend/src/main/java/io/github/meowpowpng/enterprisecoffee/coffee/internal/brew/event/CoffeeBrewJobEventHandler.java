package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewJobRepository;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Component
class CoffeeBrewJobEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CoffeeBrewJobEventHandler.class);

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
        log.debug("Started coffee brew job (id={})", id);
        try {
            repository.create(event.job());
        }
        catch (Exception e) {
            log.error("Failed to persist coffee brew job (id={})", id, e);
        }
    }

    @Async
    @Transactional
    @EventListener
    public void onProgressUpdated(CoffeeBrewJobProgressUpdatedEvent event) {
        var id = event.job().id().value();
        log.debug("Coffee brew job progress updated (id={}, progress={})",
                id,
                event.previousProgress() + "->" +event.job().progress()
        );
        try {
            repository.update(event.job());
        }
        catch (Exception e) {
            log.error("Failed to persist coffee brew job (id={})", id, e);
        }
    }

    @Async
    @Transactional
    @EventListener
    public void onFinished(CoffeeBrewJobFinishedEvent event) {
        var id = event.job().id().value();
        log.debug("Coffee brew job finished (id={}, progress={})",
                id,
                event.job().progress()
        );
        try {
            repository.update(event.job());
        }
        catch (Exception e) {
            log.error("Failed to persist coffee brew job (id={})", id, e);
        }
    }
}
