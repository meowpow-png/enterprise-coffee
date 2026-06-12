package io.github.meowpowpng.enterprisecoffee.infra;

import java.util.Objects;

/**
 * Message returned to the client.
 */
public record MessageResponse(String message) {

    /**
     * Creates a message response.
     *
     * @param message response message
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    public MessageResponse {
        Objects.requireNonNull(message, "message must not be null");
    }
}
