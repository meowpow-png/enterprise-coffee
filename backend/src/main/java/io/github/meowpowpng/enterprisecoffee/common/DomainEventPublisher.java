package io.github.meowpowpng.enterprisecoffee.common;

/**
 * Publishes domain events to the system.
 */
public interface DomainEventPublisher {

    /**
     * Publishes the given event.
     *
     * @param event the event to publish
     *
     * @throws NullPointerException if {@code event} is {@code null}
     */
    void publish(DomainEvent event);
}
