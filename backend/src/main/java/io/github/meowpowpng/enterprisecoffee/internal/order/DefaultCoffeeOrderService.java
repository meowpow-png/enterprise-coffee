package io.github.meowpowpng.enterprisecoffee.internal.order;

import io.github.meowpowpng.enterprisecoffee.api.ClientOrderRequest;
import io.github.meowpowpng.enterprisecoffee.api.ClientOrderResponse;
import io.github.meowpowpng.enterprisecoffee.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.internal.brew.CoffeeBrewJob;
import io.github.meowpowpng.enterprisecoffee.internal.brew.event.CoffeeBrewJobFinishedEvent;
import io.github.meowpowpng.enterprisecoffee.internal.brew.CoffeeBrewTracker;
import io.github.meowpowpng.enterprisecoffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.internal.client.MachineOrderResponse;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Default {@link CoffeeOrderService} implementation.
 */
@Service
public class DefaultCoffeeOrderService implements CoffeeOrderService {

    private static final Logger log = LoggerFactory.getLogger(DefaultCoffeeOrderService.class);

    private final CoffeeMachineClient client;
    private final DomainEventPublisher publisher;
    private final CoffeeBrewTracker tracker;

    DefaultCoffeeOrderService(
            CoffeeMachineClient client,
            DomainEventPublisher publisher,
            CoffeeBrewTracker tracker
    ) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(publisher, "publisher must not be null");
        Objects.requireNonNull(tracker, "tracker must not be null");

        this.client = client;
        this.publisher = publisher;
        this.tracker = tracker;
    }

    @Override
    public ClientOrderResponse order(ClientOrderRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        CoffeeBrewJob job = CoffeeBrewJob.create();
        MachineOrderResponse response;
        try {
            response = client.order(request.type());
        }
        catch (CoffeeMachineException e) {
            job.fail();

            publisher.publish(new CoffeeBrewJobFinishedEvent(job));

            var message = "coffee machine is not responding";
            throw new CoffeeOrderProcessingException(message, e);
        }
        if (response.isAccepted()) {
            tracker.track(job);
            return ClientOrderResponse.accepted();
        }
        if (response.isRejected()) {
            throw new CoffeeOrderProcessingException("coffee machine is busy");
        }
        if (response.isInvalid()) {
            throw new CoffeeOrderInvalidException("coffee order is invalid");
        }
        var message = "Unexpected response from coffee machine (status=%s)";
        throw new IllegalStateException(message.formatted(response));
    }
}
