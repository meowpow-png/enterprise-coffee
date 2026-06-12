package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

import io.github.meowpowpng.enterprisecoffee.coffee.api.ClientOrderRequest;
import io.github.meowpowpng.enterprisecoffee.coffee.api.ClientOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.CoffeeJob;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.job.CoffeeTracker;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.MachineOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

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
    private final CoffeeOrderRepository repository;
    private final CoffeeTracker tracker;

    DefaultCoffeeOrderService(
            CoffeeMachineClient client,
            CoffeeOrderRepository repository,
            CoffeeTracker tracker
    ) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(repository, "repository must not be null");
        Objects.requireNonNull(tracker, "tracker must not be null");

        this.client = client;
        this.repository = repository;
        this.tracker = tracker;
    }

    @Override
    public ClientOrderResponse order(ClientOrderRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        var type = CoffeeType.valueOf(request.type());
        var order = CoffeeOrder.create(type);
        var orderId = order.id();

        MachineOrderResponse response;
        try {
            response = client.order(type);
        }
        catch (CoffeeMachineException e) {
            log.warn("Coffee order failed because machine is unavailable (id={})",
                    orderId.value(),
                    e
            );
            repository.save(order.fail());

            var message = "coffee machine is not responding";
            throw new CoffeeOrderProcessingException(message, e);
        }
        if (response.isAccepted()) {
            repository.save(order.accept());

            log.info("Coffee order accepted (id={})", orderId.value());

            var job = CoffeeJob.create(order.id());
            log.info("Coffee brew job created (id={})", job.id().value());

            tracker.track(job);
            return ClientOrderResponse.accepted();
        }
        if (response.isRejected()) {
            repository.save(order.reject());

            log.info("Coffee order rejected because machine is busy (id={})", orderId.value());
            throw new CoffeeOrderProcessingException("coffee machine is busy");
        }
        if (response.isInvalid()) {
            repository.save(order.markInvalid());

            log.info("Coffee order rejected because request is invalid (id={})", orderId.value());
            throw new CoffeeOrderInvalidException("coffee order is invalid");
        }
        var message = "Unexpected response from coffee machine (status=%s)";
        throw new IllegalStateException(message.formatted(response));
    }
}
