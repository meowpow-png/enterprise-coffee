package io.github.meowpowpng.enterprisecoffee.coffee.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

@ConfigurationProperties("coffee")
public record CoffeeProperties(
        @NotBlank
        String machineUrl,
        @NotNull
        @Positive
        Duration pollingInterval,
        @NotNull
        @Positive
        Duration brewTimeout,
        @NotNull
        @Positive
        Duration connectTimeout,
        @NotNull
        @Positive
        Duration readTimeout
) {}
