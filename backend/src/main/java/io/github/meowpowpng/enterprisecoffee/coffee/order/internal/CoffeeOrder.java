package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

import java.util.Objects;
import java.util.UUID;

/**
 * Coffee order submitted to the machine.
 */
public final class CoffeeOrder {

    private final Id id;
    private final CoffeeType type;
    private final Status status;

    private CoffeeOrder(Id id, CoffeeType type, Status status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Creates a new pending coffee order.
     *
     * @param type requested coffee type
     *
     * @throws NullPointerException if {@code type} is {@code null}
     */
    public static CoffeeOrder create(CoffeeType type) {
        return new CoffeeOrder(Id.generate(), type, Status.PENDING);
    }

    /**
     * Reconstructs an existing coffee order.
     *
     * @param id order identifier
     * @param type requested coffee type
     * @param status order status
     *
     * @throws NullPointerException if any argument is {@code null}
     */
    static CoffeeOrder restore(Id id, CoffeeType type, Status status) {
        return new CoffeeOrder(id, type, status);
    }

    /**
     * Returns the order identifier.
     */
    public Id id() {
        return id;
    }

    /**
     * Returns the requested coffee type.
     */
    public CoffeeType type() {
        return type;
    }

    /**
     * Returns the current order status.
     */
    public Status status() {
        return status;
    }

    /**
     * Returns a copy of this order with accepted status.
     * <p>
     * The order id and coffee type are preserved.
     */
    public CoffeeOrder accept() {
        return withStatus(Status.ACCEPTED);
    }

    /**
     * Returns a copy of this order with rejected status.
     * <p>
     * The order id and coffee type are preserved.
     */
    public CoffeeOrder reject() {
        return withStatus(Status.REJECTED);
    }

    /**
     * Returns a copy of this order with invalid status.
     * <p>
     * The order id and coffee type are preserved.
     */
    public CoffeeOrder markInvalid() {
        return withStatus(Status.INVALID);
    }

    /**
     * Returns a copy of this order with failed status.
     * <p>
     * The order id and coffee type are preserved.
     */
    public CoffeeOrder fail() {
        return withStatus(Status.FAILED);
    }

    private CoffeeOrder withStatus(Status status) {
        return new CoffeeOrder(id, type, status);
    }

    /**
     * Coffee order identifier.
     */
    public record Id(UUID value) {

        /**
         * Creates a new order identifier.
         *
         * @param value identifier value
         *
         * @throws NullPointerException if {@code value} is {@code null}
         */
        public Id {
            Objects.requireNonNull(value, "value must not be null");
        }

        /**
         * Creates a new random order identifier.
         *
         * @return created identifier
         */
        public static Id generate() {
            return new Id(UUID.randomUUID());
        }
    }

    /**
     * Status of a coffee order.
     */
    public enum Status {

        PENDING,
        ACCEPTED,
        REJECTED,
        INVALID,
        FAILED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoffeeOrder that)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(type, that.type)
                && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, status);
    }
}
