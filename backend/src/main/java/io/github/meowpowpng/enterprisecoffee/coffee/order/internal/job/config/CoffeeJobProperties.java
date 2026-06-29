package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import org.hibernate.validator.constraints.time.DurationMin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

@Validated
@ConfigurationProperties("coffee.order.job")
public record CoffeeJobProperties(
        @NotNull
        @DurationMin(millis = 100)
        Duration pollingInterval,
        @NotNull
        @DurationMin(seconds = 1)
        Duration timeout
) {}
