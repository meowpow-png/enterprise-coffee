package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import jakarta.validation.constraints.NotBlank;

/**
 * Coffee order request submitted by client.
 */
public record CoffeeOrderRequest(
        @NotBlank(message = "coffee type is required")
        String type
) {}
