package io.github.meowpowpng.enterprisecoffee.coffee.internal.client;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * HTTP client for communicating with the coffee machine.
 */
@Component
class HttpCoffeeMachineClient implements CoffeeMachineClient {

    private static final CoffeeMachineClientLogger log = new CoffeeMachineClientLogger();

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

        log.statusReceived(response.status().name());
        return response;
    }

    @Override
    public MachineOrderResponse order(CoffeeType type) {
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
        log.orderReceived(response.toString());
        return response;
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
        log.progressReceived(
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

                log.requestFailed(op.name, exception);
                throw exception;
            }
            return response;
        }
        catch (RestClientException | IllegalArgumentException e) {
            log.requestFailed(op.name, e);
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

    private record MachineProgressPayload(String type, int progress) {}
}
