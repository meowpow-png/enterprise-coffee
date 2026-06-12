package io.github.meowpowpng.enterprisecoffee.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CoffeeConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
