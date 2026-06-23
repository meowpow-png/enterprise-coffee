package io.github.meowpowpng.enterprisecoffee.support;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.Executor;

@TestConfiguration
class TestAsyncConfiguration {

    @Bean
    @Primary
    @Qualifier("asyncExecutor")
    Executor testAsyncExecutor() {
        return Runnable::run;
    }
}
