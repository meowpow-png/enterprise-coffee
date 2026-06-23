package io.github.meowpowpng.enterprisecoffee.infra.config;

import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

@IntegrationTest
class ApplicationConfigurationTest {

    @Test
    @DisplayName("Should register application properties when application configuration is loaded")
    void should_RegisterApplicationProperties_when_ApplicationConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(ApplicationConfiguration.class)
                .hasBean(ApplicationProperties.class)
                .doesNotFail();
    }

    @Test
    @DisplayName("Should register clock when application configuration is loaded")
    void should_RegisterClock_when_ApplicationConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(ApplicationConfiguration.class)
                .hasBean(Clock.class)
                .doesNotFail();
    }
}
