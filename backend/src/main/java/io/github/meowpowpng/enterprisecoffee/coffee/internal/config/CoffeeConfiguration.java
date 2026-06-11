package io.github.meowpowpng.enterprisecoffee.coffee.internal.config;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.CoffeeBrewTracker;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.DefaultThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.brew.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.coffee.internal.client.CoffeeMachineClient;
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
    CoffeeBrewTracker coffeeBrewTracker(
            CoffeeMachineClient client,
            DomainEventPublisher publisher,
            ThreadSleeper sleeper,
            CoffeeProperties properties,
            Clock clock
    ) {
        return new CoffeeBrewTracker(
                client,
                publisher,
                sleeper,
                properties.brewTimeout(),
                clock
        );
    }

    @Bean
    RestClient restClient(CoffeeProperties properties) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
                .baseUrl(properties.machineUrl())
                .requestFactory(factory)
                .build();
    }
}
