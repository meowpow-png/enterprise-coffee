package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineCoffeeProgress;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.event.CoffeeJobEvents;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;

import org.springframework.scheduling.annotation.Async;

import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks the status and progress of a coffee job.
 */
public class CoffeeJobTracker {

    private final CoffeeMachineClient client;
    private final DomainEventPublisher publisher;
    private final ThreadSleeper sleeper;
    private final Duration timeout;
    private final Clock clock;

    public CoffeeJobTracker(
            CoffeeMachineClient client,
            DomainEventPublisher publisher,
            ThreadSleeper sleeper,
            Duration timeout,
            Clock clock
    ) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(publisher, "publisher must not be null");
        Objects.requireNonNull(sleeper, "properties must not be null");
        Objects.requireNonNull(timeout, "jobTimeout must not be null");
        Objects.requireNonNull(clock, "clock must not be null");

        this.client = client;
        this.publisher = publisher;
        this.sleeper = sleeper;
        this.timeout = timeout;
        this.clock = clock;
    }

    /**
     * Tracks the specified coffee job
     * until it completes or fails.
     *
     * @param job coffee job to track
     *
     * @throws IllegalStateException if the job is not pending
     */
    @Async
    public void track(CoffeeJob job) {
        var id = job.id().value();

        Logger.logTrackingStarted(id);
        start(job);

        var deadline = clock.instant().plus(timeout);
        while (!timedOut(deadline)) {
            MachineCoffeeProgress progressResponse;
            try {
                progressResponse = client.progress();
            }
            catch (CoffeeMachineException e) {
                Logger.logCommunicationFailed(id, e);
                finish(job, job::fail);
                return;
            }
            var progress = progressResponse.progress();

            if (progress.value() == 100) {
                Logger.logTrackingCompleted(id);

                finish(job, job::complete);
                return;
            }
            updateProgress(job, progress);
            try {
                sleeper.sleep();
            }
            catch (IllegalStateException e) {
                Logger.logTrackingInterrupted(id, job.status(), progress.value());
                finish(job, job::fail);
                return;
            }
        }
        Logger.logTrackingTimedOut(id, job.progress().value());
        finish(job, job::fail);
    }

    private void updateProgress(CoffeeJob job, Progress newProgress) {
        var previousProgress = job.progress();
        if (previousProgress.equals(newProgress)) {
            return;
        }
        job.updateProgress(newProgress);

        var event = CoffeeJobEvents.progressUpdated(job, previousProgress);
        publisher.publish(event);
    }

    private void start(CoffeeJob job) {
        job.start();
        publisher.publish(CoffeeJobEvents.started(job));
    }

    private void finish(CoffeeJob job, Runnable action) {
        action.run();
        publisher.publish(CoffeeJobEvents.finished(job));
    }

    private boolean timedOut(Instant deadline) {
        var now = clock.instant();
        return now.equals(deadline) || now.isAfter(deadline);
    }

    private static final class Logger {

        private static final org.slf4j.Logger log =
                LoggerFactory.getLogger(CoffeeJobTracker.class);

        private enum Event {
            TRACKING_STARTED,
            TRACKING_COMPLETED,
            TRACKING_TIMED_OUT,
            TRACKING_INTERRUPTED,
            COMMUNICATION_FAILED
        }

        static void logTrackingStarted(UUID id) {
            debug(Event.TRACKING_STARTED, id);
        }

        static void logTrackingCompleted(UUID id) {
            log.info("Coffee job completed (id={})", id);
            debug(Event.TRACKING_COMPLETED, id);
        }

        static void logTrackingTimedOut(UUID id, int progress) {
            log.warn("event={} id={} progress={}",
                    Event.TRACKING_TIMED_OUT,
                    id,
                    progress
            );
        }

        static void logTrackingInterrupted(UUID id, CoffeeJob.Status status, int progress) {
            log.warn("event={} id={} status={} progress={}",
                    Event.TRACKING_INTERRUPTED,
                    id,
                    status,
                    progress
            );
        }

        static void logCommunicationFailed(UUID id, Throwable cause) {
            log.error("event={} id={} error={}",
                    Event.COMMUNICATION_FAILED,
                    id,
                    cause.getClass().getSimpleName(),
                    cause
            );
        }

        private static void debug(Event event, UUID id) {
            log.debug("event={} id={}", event, id);
        }
    }
}
