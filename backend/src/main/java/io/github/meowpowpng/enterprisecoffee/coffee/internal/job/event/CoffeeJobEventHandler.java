package io.github.meowpowpng.enterprisecoffee.coffee.internal.job.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.CoffeeJobRepository;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class CoffeeJobEventHandler {

    private static final CoffeeJobEventHandlerLogger log = new CoffeeJobEventHandlerLogger();

    private final CoffeeJobRepository repository;

    CoffeeJobEventHandler(CoffeeJobRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    @Async
    @EventListener
    public void onStarted(CoffeeJobEvents.Started event) {
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
    @EventListener
    public void onProgressUpdated(CoffeeJobEvents.ProgressUpdated event) {
        var id = event.job().id().value();
        log.progressUpdated(id,
                event.previous().value(),
                event.job().progress().value()
        );
        try {
            repository.update(event.job());
        }
        catch (Exception e) {
            log.persistenceFailed(id, e);
        }
    }

    @Async
    @EventListener
    public void onFinished(CoffeeJobEvents.Finished event) {
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
