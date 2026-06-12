package io.github.meowpowpng.enterprisecoffee.coffee.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.event.CoffeeJobFinishedEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.event.CoffeeJobProgressUpdatedEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.event.CoffeeJobStartedEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.MachineProgressResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;

import org.springframework.scheduling.annotation.Async;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Tracks the status and progress
 * of a coffee-brewing job.
 */
public class CoffeeTracker {

    private static final CoffeeTrackerLogger log = new CoffeeTrackerLogger();

    private final CoffeeMachineClient client;
    private final DomainEventPublisher publisher;
    private final ThreadSleeper sleeper;
    private final Duration brewTimeout;
    private final Clock clock;

    public CoffeeTracker(
            CoffeeMachineClient client,
            DomainEventPublisher publisher,
            ThreadSleeper sleeper,
            Duration brewTimeout,
            Clock clock
    ) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(publisher, "publisher must not be null");
        Objects.requireNonNull(sleeper, "properties must not be null");
        Objects.requireNonNull(brewTimeout, "brewTimeout must not be null");
        Objects.requireNonNull(clock, "clock must not be null");

        this.client = client;
        this.publisher = publisher;
        this.sleeper = sleeper;
        this.brewTimeout = brewTimeout;
        this.clock = clock;
    }

    /**
     * Tracks the specified brewing job
     * until it completes or fails.
     *
     * @param job brewing job to track
     *
     * @throws IllegalStateException if the job is not pending
     */
    @Async
    public void track(CoffeeJob job) {
        var id = job.id().value();

        log.trackingStarted(id);
        start(job);

        var deadline = clock.instant().plus(brewTimeout);
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

        publisher.publish(new CoffeeJobProgressUpdatedEvent(
                job,
                previousProgress.value()
        ));
    }

    private void start(CoffeeJob job) {
        job.start();
        publisher.publish(new CoffeeJobStartedEvent(job));
    }

    private void finish(CoffeeJob job, Runnable action) {
        action.run();
        publisher.publish(new CoffeeJobFinishedEvent(job));
    }

    private boolean timedOut(Instant deadline) {
        var now = clock.instant();
        return now.equals(deadline) || now.isAfter(deadline);
    }
}
