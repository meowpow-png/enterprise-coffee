package io.github.meowpowpng.enterprisecoffee.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Validated
@ConfigurationProperties("app")
public record ApplicationProperties(
        @NotEmpty(message = "Allowed origins must not be empty")
        List<@AllowedOrigin String> allowedOrigins
) {}
