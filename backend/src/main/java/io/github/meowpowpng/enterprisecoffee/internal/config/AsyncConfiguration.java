package io.github.meowpowpng.enterprisecoffee.internal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfiguration {

    @Bean
    Executor coffeeBrewTrackerExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
