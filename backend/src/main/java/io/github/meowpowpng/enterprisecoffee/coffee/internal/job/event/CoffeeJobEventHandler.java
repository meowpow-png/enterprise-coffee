package io.github.meowpowpng.enterprisecoffee.coffee.internal.job.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.CoffeeJob;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.CoffeeJobRepository;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.CoffeeJobTracker;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.order.event.CoffeeOrderEvents;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
class CoffeeJobEventHandler {

    private static final CoffeeJobEventHandlerLogger log = new CoffeeJobEventHandlerLogger();

    private final CoffeeJobRepository repository;
    private final CoffeeJobTracker tracker;

    CoffeeJobEventHandler(CoffeeJobRepository repository, CoffeeJobTracker tracker) {
        Objects.requireNonNull(repository, "repository must not be null");
        Objects.requireNonNull(tracker, "tracker must not be null");

        this.repository = repository;
        this.tracker = tracker;
    }

    /**
     * <strong>Implementation Note:</strong>
     * Triggered after {@link CoffeeOrderEvents.Accepted}
     * event transaction commits to ensure that the
     * referenced order is visible in the database.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStored(CoffeeOrderEvents.Stored event) {
        var orderId = event.order().id();
        var job = CoffeeJob.create(orderId);

        log.created(job.id().value(), orderId.value());

        tracker.track(job);
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
