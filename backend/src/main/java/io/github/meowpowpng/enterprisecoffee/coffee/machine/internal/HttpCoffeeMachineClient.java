package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.*;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.springframework.http.HttpStatusCode;
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
    public MachineStatusResponse status() {
        var operation = CallOperation.of("status", () -> restClient.get()
                .uri("/status")
                .retrieve()
                .body(MachineStatusResponse.class),
                "Failed to retrieve coffee machine status"
        );
        var response = callMachine(operation);

        Logger.logStatusReceived(response.status().name());
        return response;
    }

    @Override
    public MachineOrderResult order(CoffeeType type) {
        Objects.requireNonNull(type, "type must not be null");

        var operation = CallOperation.of("order", () -> restClient.post()
                .uri("/order")
                .body(new MachineOrderRequest(type.value()))
                .exchange((ignored, clientResponse) ->
                        new MachineOrderResponse(clientResponse.getStatusCode())
                ),
                "Failed to submit coffee order"
        );
        var response = callMachine(operation);

        var statusCode = response.status.value();
        Logger.logOrderReceived(String.valueOf(statusCode));

        return switch (statusCode) {
            case 202 -> MachineOrderResult.ACCEPTED;
            case 409 -> MachineOrderResult.BUSY;
            case 400 -> MachineOrderResult.INVALID;
            default -> throw new IllegalStateException(
                    "Unexpected machine order response: HTTP " + statusCode
            );
        };
    }

    @Override
    public MachineProgressResponse progress() {
        var operation = CallOperation.of("progress", () -> restClient.get()
                .uri("/progress")
                .exchange((ignored, response) -> {
                    var payload = response.bodyTo(MachineProgressPayload.class);

                    if (payload == null) {
                        return null;
                    }
                    var type = payload.type();
                    return new MachineProgressResponse(
                            !type.isBlank() ? new CoffeeType(type) : null,
                            Progress.of(payload.progress())
                    );
                }),
                "Failed to retrieve coffee machine progress"
        );
        var response = callMachine(operation);
        Logger.logProgressReceived(
                response.type(),
                response.progress().value()
        );
        return response;
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

    private record MachineOrderResponse(HttpStatusCode status) {}

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
