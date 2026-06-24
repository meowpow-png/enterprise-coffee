package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class CoffeeOrderControllerConfiguration {

    @Bean
    StubCoffeeOrderService stubCoffeeOrderService() {
        return new StubCoffeeOrderService();
    }

    @Bean
    @Primary
    CoffeeOrderService testCoffeeOrderService(StubCoffeeOrderService service) {
        return service;
    }
}
