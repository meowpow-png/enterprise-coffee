package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.event;

import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.event.CoffeeOrderEvents;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJob;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobRepository;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobTracker;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import org.slf4j.LoggerFactory;

import java.util.UUID;

@Component
class CoffeeJobEventHandler {

    private final CoffeeJobRepository repository;
    private final CoffeeJobTracker tracker;

    CoffeeJobEventHandler(CoffeeJobRepository repository, CoffeeJobTracker tracker) {
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

        Logger.logCreated(job.id().value(), orderId.value());

        tracker.track(job);
    }

    @Async
    @EventListener
    public void onStarted(CoffeeJobEvents.Started event) {
        var id = event.job().id().value();
        Logger.logStarted(id);
        try {
            repository.create(event.job());
        }
        catch (Exception e) {
            Logger.logPersistenceFailed(id, e);
        }
    }

    @Async
    @EventListener
    public void onProgressUpdated(CoffeeJobEvents.ProgressUpdated event) {
        var id = event.job().id().value();
        Logger.logProgressUpdated(id,
                event.previous().value(),
                event.job().progress().value()
        );
        try {
            repository.update(event.job());
        }
        catch (Exception e) {
            Logger.logPersistenceFailed(id, e);
        }
    }

    @Async
    @EventListener
    public void onFinished(CoffeeJobEvents.Finished event) {
        var id = event.job().id().value();
        Logger.logFinished(id, event.job().progress().value());
        try {
            repository.update(event.job());
        }
        catch (Exception e) {
            Logger.logPersistenceFailed(id, e);
        }
    }

    private static final class Logger {

        private static final org.slf4j.Logger log =
                LoggerFactory.getLogger(CoffeeJobEventHandler.class);

        private enum Event {
            JOB_CREATED,
            JOB_STARTED,
            JOB_PROGRESS_UPDATED,
            JOB_FINISHED,
            JOB_PERSISTENCE_FAILED
        }

        static void logCreated(UUID jobId, UUID orderId) {
            log.info("event={} job_id={} order_id={}",
                    Event.JOB_CREATED,
                    jobId,
                    orderId
            );
        }

        static void logStarted(UUID id) {
            log.debug("event={} id={}", Event.JOB_STARTED, id);
        }

        static void logProgressUpdated(UUID id, int previousProgress, int currentProgress) {
            var progress = previousProgress + "->" + currentProgress;
            debugProgress(Event.JOB_PROGRESS_UPDATED, id, progress);
        }

        static void logFinished(UUID id, int progress) {
            debugProgress(Event.JOB_FINISHED, id, progress);
        }

        static void logPersistenceFailed(UUID id, Exception exception) {
            log.error("event={} id={} error={}",
                    Event.JOB_PERSISTENCE_FAILED,
                    id,
                    exception.getClass().getSimpleName(),
                    exception
            );
        }

        private static void debugProgress(Event event, UUID id, Object progress) {
            log.debug("event={} id={} progress={}", event, id, progress);
        }
    }
}
