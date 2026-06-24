package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobTracker;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import org.mockito.Mockito;

@TestConfiguration
public class CoffeeOrderServiceConfiguration {

    @Bean
    @Primary
    CoffeeJobTracker testCoffeeJobTracker() {
        return Mockito.mock(CoffeeJobTracker.class);
    }
}
