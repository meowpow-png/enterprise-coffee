package io.github.meowpowpng.enterprisecoffee.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("coffee")
public record ApplicationProperties() {}
