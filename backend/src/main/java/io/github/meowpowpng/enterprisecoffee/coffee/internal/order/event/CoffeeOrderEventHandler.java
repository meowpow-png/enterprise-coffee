package io.github.meowpowpng.enterprisecoffee.coffee.internal.order.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.order.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.order.CoffeeOrderRepository;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Component
class CoffeeOrderEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CoffeeOrderEventHandler.class);

    private final CoffeeOrderRepository repository;
    private final DomainEventPublisher publisher;

    CoffeeOrderEventHandler(CoffeeOrderRepository repository, DomainEventPublisher publisher) {
        Objects.requireNonNull(repository, "repository must not be null");
        Objects.requireNonNull(publisher, "publisher must not be null");

        this.repository = repository;
        this.publisher = publisher;
    }

    /**
     * <strong>Implementation Note:</strong>
     * Marked as transactional because {@link CoffeeOrderEvents.Stored}
     * event is consumed by {@code AFTER_COMMIT} transactional listener.
     */
    @Async
    @Transactional
    @EventListener
    public void onAccepted(CoffeeOrderEvents.Accepted event) {
        log.info("Coffee order accepted (id={})", event.order().id().value());
        if (!persist(event.order())) {
            return;
        }
        publisher.publish(CoffeeOrderEvents.store(event.order()));
    }

    @Async
    @EventListener
    public void onRejected(CoffeeOrderEvents.Rejected event) {
        persist(event.order());
    }

    @Async
    @EventListener
    public void onInvalid(CoffeeOrderEvents.Invalid event) {
        persist(event.order());
    }

    @Async
    @EventListener
    public void onFailed(CoffeeOrderEvents.Failed event) {
        persist(event.order());
    }

    private boolean persist(CoffeeOrder order) {
        try {
            repository.save(order);
            return true;
        }
        catch (Exception e) {
            log.error("Failed to persist coffee order (id={})",
                    order.id().value(),
                    e
            );
            return false;
        }
    }
}
