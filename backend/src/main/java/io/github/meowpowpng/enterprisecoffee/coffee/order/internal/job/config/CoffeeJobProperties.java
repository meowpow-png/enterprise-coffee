package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

@ConfigurationProperties("coffee.order.job")
public record CoffeeJobProperties(
        @NotNull
        @Positive
        Duration pollingInterval,
        @NotNull
        @Positive
        Duration timeout
) {}
