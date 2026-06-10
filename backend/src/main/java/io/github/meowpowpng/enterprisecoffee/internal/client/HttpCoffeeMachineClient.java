package io.github.meowpowpng.enterprisecoffee.internal.client;

import io.github.meowpowpng.enterprisecoffee.model.CoffeeType;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
        Supplier<MachineStatusResponse> operation = () -> restClient.get()
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

        Supplier<MachineOrderResponse> operation = () -> restClient.post()
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
        Supplier<MachineProgressResponse> operation = () -> restClient.get()
                .uri("/progress")
                .retrieve()
                .body(MachineProgressResponse.class);

        return callMachine(
                operation,
                "Failed to retrieve coffee machine progress",
                "Coffee machine returned an empty progress response"
        );
    }

    private static <T> T callMachine(
            Supplier<T> operation,
            String failureMessage,
            String emptyResponseMessage
    ) {
        try {
            var response = operation.get();

            if (response == null) {
                throw new CoffeeMachineException(emptyResponseMessage);
            }
            return response;
        }
        catch (RestClientException e) {
            throw new CoffeeMachineException(failureMessage, e);
        }
    }
}
