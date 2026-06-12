package io.github.meowpowpng.enterprisecoffee.coffee.internal.job.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

final class CoffeeJobEventHandlerLogger {

    @SuppressWarnings("LoggerInitializedWithForeignClass")
    private static final Logger log = LoggerFactory.getLogger(CoffeeJobEventHandler.class);

    private enum Event {
        JOB_STARTED,
        JOB_PROGRESS_UPDATED,
        JOB_FINISHED,
        JOB_PERSISTENCE_FAILED
    }

    void started(UUID id) {
        log.debug("event={} id={}", Event.JOB_STARTED, id);
    }

    void progressUpdated(UUID id, int previousProgress, int currentProgress) {
        var progress = previousProgress + "->" + currentProgress;
        debugProgress(Event.JOB_PROGRESS_UPDATED, id, progress);
    }

    void finished(UUID id, int progress) {
        debugProgress(Event.JOB_FINISHED, id, progress);
    }

    void persistenceFailed(UUID id, Exception exception) {
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
