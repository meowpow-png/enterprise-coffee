package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

final class CoffeeBrewTrackerLogger {

    @SuppressWarnings("LoggerInitializedWithForeignClass")
    private static final Logger log = LoggerFactory.getLogger(CoffeeBrewTracker.class);

    private enum Event {
        TRACKING_STARTED,
        TRACKING_COMPLETED,
        TRACKING_TIMED_OUT,
        TRACKING_INTERRUPTED,
        COMMUNICATION_FAILED
    }

    void trackingStarted(UUID id) {
        debug(Event.TRACKING_STARTED, id);
    }

    void trackingCompleted(UUID id) {
        log.info("Coffee brew job completed (id={})", id);
        debug(Event.TRACKING_COMPLETED, id);
    }

    void trackingTimedOut(UUID id, int progress) {
        log.warn("event={} id={} progress={}",
                Event.TRACKING_TIMED_OUT,
                id,
                progress
        );
    }

    void trackingInterrupted(UUID id, CoffeeBrewJob.Status status, int progress) {
        log.warn("event={} id={} status={} progress={}",
                Event.TRACKING_INTERRUPTED,
                id,
                status,
                progress
        );
    }

    void communicationFailed(UUID id, Throwable cause) {
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
