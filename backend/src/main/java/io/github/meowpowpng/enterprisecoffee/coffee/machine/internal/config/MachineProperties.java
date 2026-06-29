package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import org.hibernate.validator.constraints.time.DurationMin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

@Validated
@ConfigurationProperties("coffee.machine")
public record MachineProperties(
        @NotBlank
        String baseUrl,
        @NotNull
        @DurationMin(millis = 100)
        Duration connectTimeout,
        @NotNull
        @DurationMin(millis = 100)
        Duration readTimeout
) {}
