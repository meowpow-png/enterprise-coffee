package io.github.meowpowpng.enterprisecoffee.infra.config;

import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class AsyncConfigurationTest {

    @Test
    @DisplayName("Should register async executor when async configuration is loaded")
    void should_RegisterAsyncExecutor_when_AsyncConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(AsyncConfiguration.class)
                .hasBean(Executor.class)
                .doesNotFail();
    }

    @Test
    @DisplayName("Should use virtual threads when async executor executes task")
    void should_UseVirtualThreads_when_AsyncExecutorExecutesTask() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(AsyncConfiguration.class)
                .withBean(Executor.class, executor -> {
                    var future = CompletableFuture.supplyAsync(
                            Thread::currentThread,
                            executor
                    );
                    assertThat(future.join().isVirtual()).isTrue();
                })
                .doesNotFail();
    }
}
