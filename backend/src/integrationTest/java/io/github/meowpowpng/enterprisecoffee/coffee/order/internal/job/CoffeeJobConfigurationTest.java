package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.config.CoffeeJobConfiguration;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.config.CoffeeJobProperties;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.time.Clock;

@IntegrationTest
class CoffeeJobConfigurationTest {

    @Test
    @DisplayName("Should register coffee job properties when coffee job configuration is loaded")
    void should_RegisterCoffeeJobProperties_when_CoffeeJobConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                .withPropertyValues(coffeeJobProperties())
                .hasBean(CoffeeJobProperties.class)
                .doesNotFail();
    }

    @Test
    @DisplayName("Should register thread sleeper when coffee job configuration is loaded")
    void should_RegisterThreadSleeper_when_CoffeeJobConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                .withPropertyValues(coffeeJobProperties())
                .hasBean(ThreadSleeper.class)
                .doesNotFail();
    }

    @Test
    @DisplayName("Should register coffee job tracker when coffee job configuration is loaded")
    void should_RegisterCoffeeJobTracker_when_CoffeeJobConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                .withPropertyValues(coffeeJobProperties())
                .hasBean(CoffeeJobTracker.class)
                .doesNotFail();
    }

    private static String[] coffeeJobProperties() {
        return new String[]{
                "coffee.order.job.polling-interval=PT1S",
                "coffee.order.job.timeout=PT10S"
        };
    }

    @TestConfiguration
    static class Configuration {

        @Bean
        CoffeeMachineClient coffeeMachineClient() {
            return Mockito.mock(CoffeeMachineClient.class);
        }

        @Bean
        DomainEventPublisher domainEventPublisher() {
            return Mockito.mock(DomainEventPublisher.class);
        }

        @Bean
        Clock clock() {
            return TestClock.create();
        }
    }
}
