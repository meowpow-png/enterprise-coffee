package io.github.meowpowpng.enterprisecoffee.infra.config;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.config.CoffeeProperties;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobTracker;
import io.github.meowpowpng.enterprisecoffee.infra.DefaultThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Clock;

@Configuration
@EnableConfigurationProperties(CoffeeProperties.class)
public class CoffeeConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ThreadSleeper threadSleeper(CoffeeProperties properties) {
        return new DefaultThreadSleeper(properties.pollingInterval());
    }

    @Bean
    CoffeeJobTracker coffeeJobTracker(
            CoffeeMachineClient client,
            DomainEventPublisher publisher,
            ThreadSleeper sleeper,
            CoffeeProperties properties,
            Clock clock
    ) {
        return new CoffeeJobTracker(
                client,
                publisher,
                sleeper,
                properties.jobTimeout(),
                clock
        );
    }
}
