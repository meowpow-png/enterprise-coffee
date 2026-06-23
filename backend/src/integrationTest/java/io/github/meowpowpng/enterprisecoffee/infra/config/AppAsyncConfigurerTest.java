package io.github.meowpowpng.enterprisecoffee.infra.config;

import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class AppAsyncConfigurerTest {

    @Test
    @DisplayName("Should provide async exception handler when async configurer is loaded")
    void should_ProvideAsyncExceptionHandler_when_AsyncConfigurerIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(AsyncConfiguration.class, AppAsyncConfigurer.class)
                .withBean(AsyncConfigurer.class, configurer -> {
                    var handler = configurer.getAsyncUncaughtExceptionHandler();
                    assertThat(handler).isInstanceOf(AsyncExceptionHandler.class);
                })
                .doesNotFail();
    }
}
