package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.config;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobTracker;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.time.Clock;
import java.time.Duration;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class CoffeeJobConfigurationTest {

    @Nested
    @DisplayName("bean")
    class BeanTests {

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
    }

    @Nested
    @DisplayName("property")
    class PropertyTests {

        @Test
        @DisplayName("Should bind properties when valid properties are provided")
        void should_BindProperties_when_ValidPropertiesAreProvided() {
            var timeout = Duration.ofSeconds(30);
            var pollingInterval = Duration.ofSeconds(2);

            Consumer<CoffeeJobProperties> assertion = p -> {
                assertThat(p.pollingInterval()).isEqualTo(pollingInterval);
                assertThat(p.timeout()).isEqualTo(timeout);
            };
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                    .withPropertyValues(coffeeJobProperties(pollingInterval, timeout))
                    .hasBean(CoffeeJobProperties.class)
                    .withBean(CoffeeJobProperties.class, assertion)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should bind timeout when valid coffee job properties are provided")
        void should_BindTimeout_when_ValidCoffeeJobPropertiesAreProvided() {
            var pollingInterval = Duration.ofSeconds(1);
            var timeout = Duration.ofSeconds(30);

            Consumer<CoffeeJobProperties> assertion = p ->
                    assertThat(p.timeout()).isEqualTo(timeout);

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                    .withPropertyValues(coffeeJobProperties(pollingInterval, timeout))
                    .hasBean(CoffeeJobProperties.class)
                    .withBean(CoffeeJobProperties.class, assertion)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should fail when polling interval is missing")
        void should_Fail_when_PollingIntervalIsMissing() {
            var properties = new String[]{
                    "coffee.order.job.timeout=PT10S"
            };
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                    .withPropertyValues(properties)
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when polling interval is zero")
        void should_Fail_when_PollingIntervalIsZero() {
            var pollingInterval = Duration.ZERO;
            var timeout = Duration.ofSeconds(10);

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                    .withPropertyValues(coffeeJobProperties(pollingInterval, timeout))
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when timeout is missing")
        void should_Fail_when_TimeoutIsMissing() {
            var properties = new String[]{
                    "coffee.order.job.polling-interval=PT1S"
            };
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                    .withPropertyValues(properties)
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when timeout is zero")
        void should_Fail_when_TimeoutIsZero() {
            var pollingInterval = Duration.ofSeconds(1);
            var timeout = Duration.ZERO;

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(CoffeeJobConfiguration.class, Configuration.class)
                    .withPropertyValues(coffeeJobProperties(pollingInterval, timeout))
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }
    }

    private static String[] coffeeJobProperties(Duration pollingInterval, Duration timeout) {
        return new String[]{
                "coffee.order.job.polling-interval=" + pollingInterval,
                "coffee.order.job.timeout=" + timeout
        };
    }

    private static String[] coffeeJobProperties() {
        return coffeeJobProperties(
                Duration.ofSeconds(1),
                Duration.ofSeconds(10)
        );
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
