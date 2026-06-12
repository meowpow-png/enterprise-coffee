package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.infra.DefaultThreadSleeper;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(CoffeeJobProperties.class)
public class CoffeeJobConfiguration {

    @Bean
    ThreadSleeper coffeeJobThreadSleeper(CoffeeJobProperties properties) {
        return new DefaultThreadSleeper(properties.pollingInterval());
    }

    @Bean
    CoffeeJobTracker coffeeJobTracker(
            CoffeeMachineClient client,
            DomainEventPublisher publisher,
            ThreadSleeper sleeper,
            CoffeeJobProperties properties,
            Clock clock
    ) {
        return new CoffeeJobTracker(
                client,
                publisher,
                sleeper,
                properties.timeout(),
                clock
        );
    }
}
