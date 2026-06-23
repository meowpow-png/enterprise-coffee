package io.github.meowpowpng.enterprisecoffee.infra.config;

import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class AsyncConfigurationTest {

    @Test
    @DisplayName("Should register async executor when async configuration is loaded")
    void should_RegisterAsyncExecutor_when_AsyncConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(AsyncConfiguration.class)
                .withBean(AsyncConfigurer.class, configurer -> {
                    var executor = configurer.getAsyncExecutor();
                    var future = CompletableFuture.supplyAsync(
                            Thread::currentThread,
                            executor
                    );
                    var thread = future.join();

                    assertThat(thread.isVirtual()).isTrue();
                })
                .doesNotFail();
    }

    @Test
    @DisplayName("Should register async exception handler when async configuration is loaded")
    void should_RegisterAsyncExceptionHandler_when_AsyncConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(AsyncConfiguration.class)
                .withBean(AsyncConfigurer.class, configurer -> {
                    var handler = configurer.getAsyncUncaughtExceptionHandler();
                    assertThat(handler).isInstanceOf(AsyncExceptionHandler.class);
                })
                .doesNotFail();
    }
}
