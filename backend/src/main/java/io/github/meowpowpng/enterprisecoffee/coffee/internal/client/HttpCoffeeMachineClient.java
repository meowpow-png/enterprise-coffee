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

    private final RestClient restClient;

    HttpCoffeeMachineClient(RestClient restClient) {
        Objects.requireNonNull(restClient, "restClient must not be null");
        this.restClient = restClient;
    }

    @Override
    public MachineStatusResponse status() {
        ResponseSupplier<MachineStatusResponse> operation = () -> restClient.get()
                .uri("/status")
                .retrieve()
                .body(MachineStatusResponse.class);

        return callMachine(
                operation,
                "Failed to retrieve coffee machine status",
                "Coffee machine returned an empty status response"
        );
    }

    @Override
    public MachineOrderResponse order(CoffeeType type) {
        Objects.requireNonNull(type, "type must not be null");

        ResponseSupplier<MachineOrderResponse> operation = () -> restClient.post()
                .uri("/order")
                .body(new MachineOrderRequest(type))
                .exchange((ignored, clientResponse) ->
                        new MachineOrderResponse(clientResponse.getStatusCode())
                );

        return callMachine(
                operation,
                "Failed to submit coffee order",
                "Coffee machine returned an empty order response"
        );
    }

    @Override
    public MachineProgressResponse progress() {
        ResponseSupplier<MachineProgressResponse> operation = () -> restClient.get()
                .uri("/progress")
                .exchange((request, response) -> {
                    var payload = response.bodyTo(MachineProgressPayload.class);

                    if (payload == null) {
                        return null;
                    }
                    return new MachineProgressResponse(
                            new CoffeeType(payload.type()),
                            Progress.of(payload.progress())
                    );
                });

        return callMachine(
                operation,
                "Failed to retrieve coffee machine progress",
                "Coffee machine returned an empty progress response"
        );
    }

    private static <T> T callMachine(
            ResponseSupplier<T> supplier,
            String failureMessage,
            String emptyResponseMessage
    ) {
        try {
            var response = supplier.get();

            if (response == null) {
                throw new CoffeeMachineException(emptyResponseMessage);
            }
            return response;
        }
        catch (RestClientException | IllegalArgumentException e) {
            throw new CoffeeMachineException(failureMessage, e);
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
