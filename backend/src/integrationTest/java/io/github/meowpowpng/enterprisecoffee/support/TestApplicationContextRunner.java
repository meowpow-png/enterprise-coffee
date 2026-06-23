package io.github.meowpowpng.enterprisecoffee.support;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public final class TestApplicationContextRunner {

    private TestApplicationContextRunner() {}

    public static Builder from(ApplicationContextRunner runner) {
        return new Builder(runner);
    }

    public static final class Builder {

        private final List<Consumer<SpringWiringTestAssertion>> assertions = new ArrayList<>();
        private ApplicationContextRunner runner;

        private Builder(ApplicationContextRunner runner) {
            this.runner = runner;
        }

        public Builder withPropertyValues(String... properties) {
            this.runner = runner.withPropertyValues(properties);
            return this;
        }

        public Builder withConfiguration(Class<?>... configs) {
            this.runner = runner.withUserConfiguration(configs);
            return this;
        }

        public Builder hasBean(Class<?> beanType) {
            assertions.add(a -> a.hasSingleBean(beanType));
            return this;
        }

        public Builder hasBean(String beanName) {
            assertions.add(a -> a.hasBean(beanName));
            return this;
        }

        public Builder doesNotHaveBean(Class<?> beanType) {
            assertions.add(a -> a.doesNotHaveBean(beanType));
            return this;
        }

        public Builder hasScheduledBean(Class<?> beanType) {
            assertions.add(a -> a.hasScheduledBean(beanType));
            return this;
        }

        public Builder usesCronFrom(String beanName) {
            assertions.add(a -> a.usesCronFrom(beanName));
            return this;
        }

        public Builder hasScheduledMethod(Class<?> beanType, String methodName) {
            assertions.add(a -> a.hasScheduledMethod(beanType, methodName));
            return this;
        }

        public Builder isTransactional(Class<?> beanType) {
            assertions.add(a -> a.isTransactionalProxy(beanType));
            return this;
        }

        public Builder hasTransactionalMethod(Class<?> beanType, String methodName) {
            assertions.add(a -> a.hasTransactionalMethod(beanType, methodName));
            return this;
        }

        public <T> Builder withBean(Class<T> beanType, Consumer<T> assertion) {
            assertions.add(a -> {
                var bean = a.getContext().getBean(beanType);
                assertion.accept(bean);
            });
            return this;
        }

        public Builder withTransactionManagement() {
            return withConfiguration(TestTransactionConfig.class);
        }

        public Builder withSchedulingEnabled() {
            return withConfiguration(TestSchedulerConfig.class);
        }

        public Builder runApplicationRunners(String... args) {
            assertions.add(a -> {
                var context = a.getContext();
                var runners = context.getBeansOfType(ApplicationRunner.class);
                var applicationArgs = new DefaultApplicationArguments(args);

                runners.values().forEach(r -> {
                    try {
                        r.run(applicationArgs);
                    }
                    catch (Exception e) {
                        throw new ApplicationRunnerException(e);
                    }
                });
            });
            return this;
        }

        public void doesNotFail() {
            runner.run(context -> {
                var assertion = SpringWiringTestAssertion.assertThatContext(context).starts();
                assertions.forEach(a -> a.accept(assertion));
            });
        }

        public void failsWithException(Class<? extends Throwable> expected) {
            runner.run(context -> {
                var startupFailure = context.getStartupFailure();
                if (startupFailure != null) {
                    assertThat(startupFailure).isInstanceOf(expected);
                    return;
                }
                var assertion = SpringWiringTestAssertion.assertThatContext(context);
                try {
                    assertions.forEach(a -> a.accept(assertion));
                }
                catch (ApplicationRunnerException e) {
                    var cause = e.getCause() != null ? e.getCause() : e;
                    assertThat(cause).isInstanceOf(expected);
                    return;
                }
                catch (RuntimeException e) {
                    assertThat(e).isInstanceOf(expected);
                    return;
                }
                var message = "Expected exception of type %s but nothing was thrown";
                throw new AssertionError(message.formatted(expected.getName()));
            });
        }
    }

    static final class ApplicationRunnerException extends RuntimeException {

        ApplicationRunnerException(Throwable cause) {
            super(cause);
        }
    }

    @TestConfiguration
    @EnableTransactionManagement
    static class TestTransactionConfig {}

    @TestConfiguration
    @EnableScheduling
    static class TestSchedulerConfig {}
}
