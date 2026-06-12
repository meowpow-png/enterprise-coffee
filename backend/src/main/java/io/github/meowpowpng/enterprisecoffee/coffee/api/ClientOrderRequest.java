package io.github.meowpowpng.enterprisecoffee.coffee.api;

import jakarta.validation.constraints.NotBlank;

/**
 * Coffee order request submitted by client.
 */
public record ClientOrderRequest(
        @NotBlank(message = "coffee type is required")
        String type
) {}
