package io.github.meowpowpng.enterprisecoffee.internal.config;

import io.github.meowpowpng.enterprisecoffee.internal.brew.CoffeeBrewTracker;
import io.github.meowpowpng.enterprisecoffee.internal.brew.DefaultThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.internal.brew.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.internal.client.CoffeeMachineClient;

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
            ThreadSleeper sleeper,
            CoffeeProperties properties,
            Clock clock
    ) {
        var timeout = properties.brewTimeout();
        return new CoffeeBrewTracker(client, sleeper, timeout, clock);
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
