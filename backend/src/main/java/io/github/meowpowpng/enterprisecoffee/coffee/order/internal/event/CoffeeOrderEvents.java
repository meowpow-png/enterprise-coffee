package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.event;

import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;

import java.util.Objects;

/**
 * Events used to signal coffee order lifecycle changes.
 */
public final class CoffeeOrderEvents {

    private CoffeeOrderEvents() {}

    /**
     * Creates an event signaling that a coffee order has been accepted.
     *
     * @param order accepted order
     *
     * @throws NullPointerException if {@code order} is {@code null}
     */
    public static Accepted accepted(CoffeeOrder order) {
        return new Accepted(order);
    }

    /**
     * Creates an event signaling that a coffee order has been stored.
     *
     * @param order stored order
     *
     * @throws NullPointerException if {@code order} is {@code null}
     */
    public static Stored store(CoffeeOrder order) {
        return new Stored(order);
    }

    /**
     * Creates an event signaling that a coffee order has been rejected.
     *
     * @param order rejected order
     *
     * @throws NullPointerException if {@code order} is {@code null}
     */
    public static Rejected rejected(CoffeeOrder order) {
        return new Rejected(order);
    }

    /**
     * Creates an event signaling that a coffee order is invalid.
     *
     * @param order invalid order
     *
     * @throws NullPointerException if {@code order} is {@code null}
     */
    public static Invalid invalid(CoffeeOrder order) {
        return new Invalid(order);
    }

    /**
     * Creates an event signaling that a coffee order has failed.
     *
     * @param order failed order
     *
     * @throws NullPointerException if {@code order} is {@code null}
     */
    public static Failed failed(CoffeeOrder order) {
        return new Failed(order);
    }

    /**
     * Event that signals that a coffee order has been accepted.
     */
    public record Accepted(CoffeeOrder order) implements DomainEvent {

        /**
         * Creates an event signaling that a coffee order has been accepted.
         *
         * @throws NullPointerException if {@code order} is {@code null}
         */
        public Accepted {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order has been stored.
     */
    public record Stored(CoffeeOrder order) implements DomainEvent {

        /**
         * Creates an event signaling that a coffee order has been stored.
         *
         * @throws NullPointerException if {@code order} is {@code null}
         */
        public Stored {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order has been rejected.
     */
    public record Rejected(CoffeeOrder order) implements DomainEvent {

        /**
         * Creates an event signaling that a coffee order has been rejected.
         *
         * @throws NullPointerException if {@code order} is {@code null}
         */
        public Rejected {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order is invalid.
     */
    public record Invalid(CoffeeOrder order) implements DomainEvent {

        /**
         * Creates an event signaling that a coffee order is invalid.
         *
         * @throws NullPointerException if {@code order} is {@code null}
         */
        public Invalid {
            Objects.requireNonNull(order, "order must not be null");
        }
    }

    /**
     * Event that signals that a coffee order has failed.
     */
    public record Failed(CoffeeOrder order) implements DomainEvent {

        /**
         * Creates an event signaling that a coffee order has failed.
         *
         * @throws NullPointerException if {@code order} is {@code null}
         */
        public Failed {
            Objects.requireNonNull(order, "order must not be null");
        }
    }
}
