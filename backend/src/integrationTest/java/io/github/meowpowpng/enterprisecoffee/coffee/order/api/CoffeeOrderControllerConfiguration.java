package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class CoffeeOrderControllerConfiguration {

    @Bean
    TestCoffeeOrderService stubCoffeeOrderService() {
        return new TestCoffeeOrderService();
    }

    @Bean
    @Primary
    CoffeeOrderService testCoffeeOrderService(TestCoffeeOrderService service) {
        return service;
    }
}
