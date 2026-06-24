package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class CoffeeMachineControllerConfiguration {

    @Bean
    @Primary
    CoffeeMachineClient testCoffeeMachineClient() {
        return new CoffeeMachineControllerTest.TestCoffeeMachineClient();
    }
}
