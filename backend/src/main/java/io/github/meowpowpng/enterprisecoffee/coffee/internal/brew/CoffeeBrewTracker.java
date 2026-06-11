package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event.CoffeeBrewJobFinishedEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event.CoffeeBrewJobProgressUpdatedEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event.CoffeeBrewJobStartedEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.MachineProgressResponse;

import org.springframework.scheduling.annotation.Async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Tracks the status and progress
 * of a coffee-brewing job.
 */
public class CoffeeBrewTracker {

    private static final Logger log = LoggerFactory.getLogger(CoffeeBrewTracker.class);

    private final CoffeeMachineClient client;
    private final DomainEventPublisher publisher;
    private final ThreadSleeper sleeper;
    private final Duration brewTimeout;
    private final Clock clock;

    public CoffeeBrewTracker(
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
    public void track(CoffeeBrewJob job) {
        job.start();
        publisher.publish(new CoffeeBrewJobStartedEvent(job));

        var deadline = clock.instant().plus(brewTimeout);
        while (!timedOut(deadline)) {
            MachineProgressResponse progressResponse;
            try {
                progressResponse = client.progress();
            }
            catch (CoffeeMachineException e) {
                log.error("Coffee machine communication failed", e);
                break;
            }
            var progress = progressResponse.progress();

            if (progress == 100) {
                job.complete();
                publisher.publish(new CoffeeBrewJobFinishedEvent(job));
                return;
            }
            updateProgress(job, progress);
            try {
                sleeper.sleep();
            }
            catch (IllegalStateException e) {
                var message = "Coffee brew tracker interrupted (status={}, progress={})";
                log.info(message, job.status(), progress);
                break;
            }
        }
        job.fail();
        publisher.publish(new CoffeeBrewJobFinishedEvent(job));
    }

    private void updateProgress(CoffeeBrewJob job, int newProgress) {
        int previousProgress = job.progress();

        job.updateProgress(newProgress);

        publisher.publish(new CoffeeBrewJobProgressUpdatedEvent(
                job,
                previousProgress
        ));
    }

    private boolean timedOut(Instant deadline) {
        var now = clock.instant();
        return now.equals(deadline) || now.isAfter(deadline);
    }
}
