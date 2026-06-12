package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.*;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import org.jspecify.annotations.Nullable;

import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * HTTP client for communicating with the coffee machine.
 */
@Component
class HttpCoffeeMachineClient implements CoffeeMachineClient {

    private final RestClient restClient;

    HttpCoffeeMachineClient(RestClient restClient) {
        Objects.requireNonNull(restClient, "restClient must not be null");
        this.restClient = restClient;
    }

    @Override
    public CoffeeMachineStatus status() {
        return callMachine(CallOperation.of("status", () -> restClient.get()
                .uri("/status")
                .exchange((ignored, response) -> {
                    var payload = response.bodyTo(MachineStatusPayload.class);
                    if (payload == null) {
                        return null;
                    }
                    Logger.logStatusReceived(payload.status());
                    return CoffeeMachineStatus.valueOf(payload.status());
                }),
        "Failed to retrieve coffee machine status"
        ));
    }

    @Override
    public MachineOrderResult order(CoffeeType type) {
        Objects.requireNonNull(type, "type must not be null");

        return callMachine(CallOperation.of("order", () -> restClient.post()
                .uri("/order")
                .body(new MachineOrderRequest(type.value()))
                .exchange((ignored, clientResponse) -> {
                    var statusCode = clientResponse.getStatusCode().value();
                    Logger.logOrderReceived(String.valueOf(statusCode));

                    return switch (statusCode) {
                        case 202 -> MachineOrderResult.ACCEPTED;
                        case 409 -> MachineOrderResult.BUSY;
                        case 400 -> MachineOrderResult.INVALID;
                        default -> throw new IllegalStateException(
                                "Unexpected machine order response: HTTP " + statusCode
                        );
                    };
                }),
                "Failed to submit coffee order"
        ));
    }

    @Override
    public MachineCoffeeProgress progress() {
        return callMachine(CallOperation.of("progress", () -> restClient.get()
                .uri("/progress")
                .exchange((ignored, response) -> {
                    var payload = response.bodyTo(MachineProgressPayload.class);

                    if (payload == null) {
                        return null;
                    }
                    var type = payload.type();
                    var coffeeType = !type.isBlank() ? new CoffeeType(type) : null;
                    var progress = payload.progress();

                    Logger.logProgressReceived(coffeeType, progress);
                    return new MachineCoffeeProgress(coffeeType, Progress.of(progress));
                }),
                "Failed to retrieve coffee machine progress"
        ));
    }

    private static <T> T callMachine(CallOperation<T> op) {
        try {
            var response = op.supplier.get();

            if (response == null) {
                var message = "Coffee machine returned an empty %s response";
                var exception = new CoffeeMachineException(message.formatted(op.name));

                Logger.logRequestFailed(op.name, exception);
                throw exception;
            }
            return response;
        }
        catch (RestClientException | IllegalArgumentException e) {
            Logger.logRequestFailed(op.name, e);
            throw new CoffeeMachineException(op.failMessage, e);
        }
    }

    private record CallOperation<T>(
            String name,
            ResponseSupplier<T> supplier,
            String failMessage
    ) {
        private static <T> CallOperation<T> of(
                String name,
                ResponseSupplier<T> supplier,
                String failMessage
        ) {
            return new CallOperation<>(name, supplier, failMessage);
        }
    }

    @FunctionalInterface
    private interface ResponseSupplier<T> extends Supplier<T> {

        @Nullable
        @Override
        T get();
    }

    private record MachineStatusPayload(String status) {}

    private record MachineProgressPayload(String type, int progress) {}

    private static final class Logger {

        private static final org.slf4j.Logger log =
                LoggerFactory.getLogger(HttpCoffeeMachineClient.class);

        private enum Event {
            MACHINE_STATUS_RECEIVED,
            MACHINE_ORDER_RECEIVED,
            MACHINE_PROGRESS_RECEIVED,
            MACHINE_REQUEST_FAILED
        }

        static void logStatusReceived(String status) {
            debugStatus(Event.MACHINE_STATUS_RECEIVED, status);
        }

        static void logOrderReceived(String status) {
            debugStatus(Event.MACHINE_ORDER_RECEIVED, status);
        }

        static void logProgressReceived(@Nullable CoffeeType type, int progress) {
            log.debug("event={} type={} progress={}",
                    Event.MACHINE_PROGRESS_RECEIVED,
                    type != null ? type.value() : "''",
                    progress
            );
        }

        static void logRequestFailed(String operation, Exception exception) {
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
}
