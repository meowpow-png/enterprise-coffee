package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

import io.github.meowpowpng.enterprisecoffee.coffee.api.ClientOrderRequest;
import io.github.meowpowpng.enterprisecoffee.coffee.api.ClientOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewJob;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewTracker;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.event.CoffeeBrewJobFinishedEvent;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.MachineOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;

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
            response = client.order(CoffeeType.valueOf(request.type()));
        }
        catch (CoffeeMachineException e) {
            log.warn("Coffee order failed because machine is unavailable", e);
            job.fail();

            publisher.publish(new CoffeeBrewJobFinishedEvent(job));

            var message = "coffee machine is not responding";
            throw new CoffeeOrderProcessingException(message, e);
        }
        if (response.isAccepted()) {
            log.info("Coffee order accepted (id={})", job.id().value());

            tracker.track(job);
            return ClientOrderResponse.accepted();
        }
        if (response.isRejected()) {
            log.info("Coffee order rejected because machine is busy");
            throw new CoffeeOrderProcessingException("coffee machine is busy");
        }
        if (response.isInvalid()) {
            log.info("Coffee order rejected because request is invalid");
            throw new CoffeeOrderInvalidException("coffee order is invalid");
        }
        var message = "Unexpected response from coffee machine (status=%s)";
        throw new IllegalStateException(message.formatted(response));
    }
}
