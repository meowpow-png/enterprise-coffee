package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

@ConfigurationProperties("coffee.machine")
public record MachineProperties(
        @NotBlank
        String baseUrl,
        @NotNull
        @Positive
        Duration connectTimeout,
        @NotNull
        @Positive
        Duration readTimeout
) {}
