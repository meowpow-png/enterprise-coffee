package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderRequest;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineOrderResult;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.event.CoffeeOrderEvents;
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
    public CoffeeOrderResponse order(CoffeeOrderRequest request) {
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
            log.warn("Coffee order failed (id={})", orderId.value(), e);
            publisher.publish(CoffeeOrderEvents.failed(order.fail()));

            var message = "coffee machine is not responding";
            throw new CoffeeOrderProcessingException(message, e);
        }
        if (result == MachineOrderResult.ACCEPTED) {
            order = order.accept();
            publisher.publish(CoffeeOrderEvents.accepted(order));

            log.info("Coffee order accepted (id={})", orderId.value());
            return CoffeeOrderResponse.accepted();
        }
        if (result == MachineOrderResult.BUSY) {
            publisher.publish(CoffeeOrderEvents.rejected(order.reject()));

            var reason = "coffee machine is busy";
            log.info("Coffee order was rejected by machine (id={} reason={})",
                    orderId.value(),
                    reason
            );
            throw new CoffeeOrderProcessingException(reason);
        }
        if (result == MachineOrderResult.INVALID) {
            publisher.publish(CoffeeOrderEvents.invalid(order.markInvalid()));

            var reason = "coffee order request is invalid";
            log.info("Coffee order was rejected by machine (id={} reason={})",
                    orderId.value(),
                    reason
            );
            throw new CoffeeOrderInvalidException(reason);
        }
        throw new IllegalStateException("Unexpected order result: " + result);
    }
}
