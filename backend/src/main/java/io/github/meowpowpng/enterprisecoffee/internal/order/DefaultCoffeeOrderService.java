package io.github.meowpowpng.enterprisecoffee.internal.order;

import io.github.meowpowpng.enterprisecoffee.api.ClientOrderRequest;
import io.github.meowpowpng.enterprisecoffee.api.ClientOrderResponse;
import io.github.meowpowpng.enterprisecoffee.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.internal.brew.CoffeeBrewJob;
import io.github.meowpowpng.enterprisecoffee.internal.brew.event.CoffeeBrewJobUpdatedEvent;
import io.github.meowpowpng.enterprisecoffee.internal.brew.CoffeeBrewJobRepository;
import io.github.meowpowpng.enterprisecoffee.internal.brew.CoffeeBrewTracker;
import io.github.meowpowpng.enterprisecoffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.model.CoffeeOrderStatus;

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
    private final CoffeeBrewJobRepository repository;
    private final DomainEventPublisher publisher;
    private final CoffeeBrewTracker tracker;

    DefaultCoffeeOrderService(
            CoffeeMachineClient client,
            CoffeeBrewJobRepository repository,
            DomainEventPublisher publisher,
            CoffeeBrewTracker tracker
    ) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(repository, "repository must not be null");
        Objects.requireNonNull(publisher, "publisher must not be null");
        Objects.requireNonNull(tracker, "tracker must not be null");

        this.client = client;
        this.repository = repository;
        this.publisher = publisher;
        this.tracker = tracker;
    }

    @Override
    public ClientOrderResponse order(ClientOrderRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        var orderStatus = CoffeeOrderStatus.RECEIVED;
        var brewJob = CoffeeBrewJob.create();

        publisher.publish(new CoffeeBrewJobUpdatedEvent(brewJob));
        try {
            var response = client.order(request.type());
            orderStatus = CoffeeOrderStatus.DISPATCHED;

            if (response.isAccepted()) {
                tracker.track(brewJob);

                if (brewJob.status() == CoffeeBrewJob.Status.FAILED) {
                    orderStatus = CoffeeOrderStatus.FAILED;
                    return ClientOrderResponse.failed("brew job failed");
                }
                orderStatus = CoffeeOrderStatus.COMPLETED;
                return ClientOrderResponse.completed();
            }
            else if (response.isRejected()) {
                orderStatus = CoffeeOrderStatus.REJECTED;
                return ClientOrderResponse.rejected("coffee machine is busy");
            }
            else if (response.isInvalid()) {
                orderStatus = CoffeeOrderStatus.REJECTED;
                return ClientOrderResponse.rejected("coffee order is invalid");
            }
            var message = "Unexpected response from coffee machine (status=%d)";
            throw new IllegalStateException(message.formatted(response));
        }
        finally {
            repository.save(brewJob);
        }
    }
}
