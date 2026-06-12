package io.github.meowpowpng.enterprisecoffee.coffee.internal.client;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

import org.jspecify.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CoffeeMachineClientLogger {

    @SuppressWarnings("LoggerInitializedWithForeignClass")
    private static final Logger log = LoggerFactory.getLogger(HttpCoffeeMachineClient.class);

    private enum Event {
        MACHINE_STATUS_RECEIVED,
        MACHINE_ORDER_RECEIVED,
        MACHINE_PROGRESS_RECEIVED,
        MACHINE_REQUEST_FAILED
    }

    void statusReceived(String status) {
        debugStatus(Event.MACHINE_STATUS_RECEIVED, status);
    }

    void orderReceived(String status) {
        debugStatus(Event.MACHINE_ORDER_RECEIVED, status);
    }

    void progressReceived(@Nullable CoffeeType type, int progress) {
        log.debug("event={} type={} progress={}",
                Event.MACHINE_PROGRESS_RECEIVED,
                type != null ? type : "''",
                progress
        );
    }

    void requestFailed(String operation, Exception exception) {
        log.warn("event={} operation={} error={}",
                Event.MACHINE_REQUEST_FAILED,
                operation,
                exception.getClass().getSimpleName(),
                exception
        );
    }

    private static void debugStatus(Event event, String status) {
        log.debug("event={} status={}", event, status);
    }
}
