package io.github.meowpowpng.enterprisecoffee.internal.brew;

import io.github.meowpowpng.enterprisecoffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.internal.client.MachineProgressResponse;

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
    private final ThreadSleeper sleeper;
    private final Duration brewTimeout;
    private final Clock clock;

    public CoffeeBrewTracker(
            CoffeeMachineClient client,
            ThreadSleeper sleeper,
            Duration brewTimeout,
            Clock clock
    ) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(sleeper, "properties must not be null");
        Objects.requireNonNull(brewTimeout, "brewTimeout must not be null");
        Objects.requireNonNull(clock, "clock must not be null");

        this.client = client;
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
    @Async("coffeeBrewTrackerExecutor")
    public void track(CoffeeBrewJob job) {
        job.start();

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

            job.updateProgress(progress);

            if (progress == 100) {
                job.complete();
                return;
            }
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
    }

    private boolean timedOut(Instant deadline) {
        var now = clock.instant();
        return now.equals(deadline) || now.isAfter(deadline);
    }
}
