package io.github.meowpowpng.enterprisecoffee.coffee.internal.order.event;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.order.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

/**
 * Events used to signal changes in coffee order lifecycle.
 */
public final class CoffeeOrderEvents {

    private CoffeeOrderEvents() {}

    /**
     * Creates an event signaling that a coffee order has been accepted.
     *
     * @param order accepted order
     */
    public static Accepted accepted(CoffeeOrder order) {
        return new Accepted(order);
    }

    public static Stored store(CoffeeOrder order) {
        return new Stored(order);
    }

    /**
     * Creates an event signaling that a coffee order has been rejected.
     *
     * @param order rejected order
     */
    public static Rejected rejected(CoffeeOrder order) {
        return new Rejected(order);
    }

    /**
     * Creates an event signaling that a coffee order is invalid.
     *
     * @param order invalid order
     */
    public static Invalid invalid(CoffeeOrder order) {
        return new Invalid(order);
    }

    /**
     * Creates an event signaling that a coffee order has failed.
     *
     * @param order failed order
     */
    public static Failed failed(CoffeeOrder order) {
        return new Failed(order);
    }

    /**
     * Event that signals that a coffee order has been accepted.
     */
    public record Accepted(CoffeeOrder order) implements DomainEvent {

        public Accepted {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order has been stored.
     */
    public record Stored(CoffeeOrder order) implements DomainEvent {

        public Stored {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order has been rejected.
     */
    public record Rejected(CoffeeOrder order) implements DomainEvent {

        public Rejected {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order is invalid.
     */
    public record Invalid(CoffeeOrder order) implements DomainEvent {

        public Invalid {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order has failed.
     */
    public record Failed(CoffeeOrder order) implements DomainEvent {

        public Failed {
            Objects.requireNonNull(order, "order must not be null");
        }
    }
}
