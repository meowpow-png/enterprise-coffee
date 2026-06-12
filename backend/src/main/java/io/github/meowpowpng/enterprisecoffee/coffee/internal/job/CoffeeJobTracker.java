package io.github.meowpowpng.enterprisecoffee.coffee.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.MachineProgressResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.event.CoffeeJobEvents;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;

import org.springframework.scheduling.annotation.Async;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Tracks the status and progress of a coffee job.
 */
public class CoffeeJobTracker {

    private static final CoffeeJobTrackerLogger log = new CoffeeJobTrackerLogger();

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

        log.trackingStarted(id);
        start(job);

        var deadline = clock.instant().plus(timeout);
        while (!timedOut(deadline)) {
            MachineProgressResponse progressResponse;
            try {
                progressResponse = client.progress();
            }
            catch (CoffeeMachineException e) {
                log.communicationFailed(id, e);
                finish(job, job::fail);
                return;
            }
            var progress = progressResponse.progress();

            if (progress.value() == 100) {
                log.trackingCompleted(id);

                finish(job, job::complete);
                return;
            }
            updateProgress(job, progress);
            try {
                sleeper.sleep();
            }
            catch (IllegalStateException e) {
                log.trackingInterrupted(id, job.status(), progress.value());
                finish(job, job::fail);
                return;
            }
        }
        log.trackingTimedOut(id, job.progress().value());
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
}
