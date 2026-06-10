package io.github.meowpowpng.enterprisecoffee.internal.client;

import org.springframework.http.HttpStatusCode;

import java.util.Objects;

/**
 * Represents a coffee order
 * response returned by the machine.
 */
public final class MachineOrderResponse {

    private final HttpStatusCode status;

    /**
     * Creates a new coffee order response.
     *
     * @param status HTTP status returned by the machine
     *
     * @throws NullPointerException if {@code status} is null
     */
    MachineOrderResponse(HttpStatusCode status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Returns whether the machine accepted the order.
     */
    public boolean isAccepted() {
        return status.value() == 202;
    }

    /**
     * Returns whether the machine rejected the order.
     */
    public boolean isRejected() {
        return status.value() == 409;
    }

    /**
     * Returns whether the request was invalid.
     */
    public boolean isInvalid() {
        return status.value() == 400;
    }

    @Override
    public String toString() {
        return "HTTP " + status.value();
    }
}
