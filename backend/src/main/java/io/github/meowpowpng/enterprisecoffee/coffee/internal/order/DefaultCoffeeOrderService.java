package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

import io.github.meowpowpng.enterprisecoffee.coffee.api.ClientOrderRequest;
import io.github.meowpowpng.enterprisecoffee.coffee.api.ClientOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.MachineOrderResult;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.order.event.CoffeeOrderEvents;
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

    DefaultCoffeeOrderService(CoffeeMachineClient client, DomainEventPublisher publisher) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(publisher, "publisher must not be null");

        this.client = client;
        this.publisher = publisher;
    }

    @Override
    public ClientOrderResponse order(ClientOrderRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        var type = new CoffeeType(request.type());
        var order = CoffeeOrder.create(type);
        var orderId = order.id();

        log.info("Coffee order created (id={}, type={})",
                orderId.value(),
                order.type().value()
        );
        MachineOrderResult result;
        try {
            result = client.order(type);
        }
        catch (CoffeeMachineException e) {
            log.warn("Coffee order failed because an exception occurred (id={})",
                    orderId.value(),
                    e
            );
            publisher.publish(CoffeeOrderEvents.failed(order.fail()));

            var message = "coffee machine is not responding";
            throw new CoffeeOrderProcessingException(message, e);
        }
        if (result.isAccepted()) {
            order = order.accept();
            publisher.publish(CoffeeOrderEvents.accepted(order));

            log.info("Coffee order accepted (id={})", orderId.value());
            return ClientOrderResponse.accepted();
        }
        if (result.isRejected()) {
            publisher.publish(CoffeeOrderEvents.rejected(order.reject()));

            log.info("Coffee order was rejected by machine (id={})", orderId.value());
            throw new CoffeeOrderProcessingException("coffee order was rejected");
        }
        if (result.isInvalid()) {
            publisher.publish(CoffeeOrderEvents.invalid(order.markInvalid()));

            log.info("Coffee order rejected because request is invalid (id={})", orderId.value());
            throw new CoffeeOrderInvalidException("coffee order is invalid");
        }
        var message = "Unexpected result from coffee machine (status=%s)";
        throw new IllegalStateException(message.formatted(result));
    }
}
