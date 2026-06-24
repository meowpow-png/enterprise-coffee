package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineStatus;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineCoffeeProgress;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineOrderResult;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrderRepository;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.JpaCoffeeOrderCrudRepository;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.support.DisableAsync;
import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import org.jspecify.annotations.NullMarked;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DisableAsync
@SpringBootTest
@IntegrationTest
class CoffeeJobTrackerTest {

    @Autowired
    private CoffeeJobTracker tracker;

    @Autowired
    private CoffeeJobRepository jobRepository;

    @Autowired
    private JpaCoffeeJobCrudRepository jobCrudRepository;

    @Autowired
    private CoffeeOrderRepository orderRepository;

    @Autowired
    private JpaCoffeeOrderCrudRepository orderCrudRepository;

    @Autowired
    private CoffeeMachineClient client;

    private TestCoffeeMachineClient testClient;

    @BeforeEach
    void setupCoffeeJobTrackerTest() {
        this.testClient = (TestCoffeeMachineClient) client;
    }

    @AfterEach
    void teardownCoffeeJobTrackerTest() {
        jobCrudRepository.deleteAll();
        orderCrudRepository.deleteAll();
    }

    @Test
    @DisplayName("Should persist coffee job when tracking begins")
    void should_PersistCoffeeJob_when_TrackingBegins() {
        var order = CoffeeOrder.create(new CoffeeType("ESPRESSO"));
        var job = CoffeeJob.create(order.id());

        orderRepository.save(order);
        testClient.progressTo(50);
        testClient.progressTo(100);

        tracker.track(job);

        var stored = jobRepository.findById(job.id());
        assertThat(stored).isPresent();
    }

    @Test
    @DisplayName("Should persist updated progress when machine reports increased progress")
    void should_PersistUpdatedProgress_when_MachineReportsIncreasedProgress() {
        var order = CoffeeOrder.create(new CoffeeType("ESPRESSO"));
        var job = CoffeeJob.create(order.id());

        orderRepository.save(order);

        testClient.progressTo(50);
        testClient.fail();

        tracker.track(job);

        var stored = jobRepository.findById(job.id());

        assertThat(stored).hasValueSatisfying(found ->
                assertThat(found.progress()).isEqualTo(Progress.of(50))
        );
    }

    @Test
    @DisplayName("Should persist completed job when machine reports 100 percent progress")
    void should_PersistCompletedJob_when_MachineReports100PercentProgress() {
        var order = CoffeeOrder.create(new CoffeeType("ESPRESSO"));
        var job = CoffeeJob.create(order.id());

        orderRepository.save(order);
        testClient.progressTo(50);
        testClient.progressTo(100);

        tracker.track(job);

        var stored = jobRepository.findById(job.id());

        assertThat(stored).hasValueSatisfying(found -> {
            assertThat(found.status()).isEqualTo(CoffeeJob.Status.COMPLETED);
            assertThat(found.progress()).isEqualTo(Progress.of(100));
        });
    }

    @Test
    @DisplayName("Should persist failed job when communication with coffee machine fails")
    void should_PersistFailedJob_when_CommunicationWithCoffeeMachineFails() {
        var order = CoffeeOrder.create(new CoffeeType("ESPRESSO"));
        var job = CoffeeJob.create(order.id());

        orderRepository.save(order);
        testClient.fail();

        tracker.track(job);

        var stored = jobRepository.findById(job.id());

        assertThat(stored).hasValueSatisfying(found ->
                assertThat(found.status()).isEqualTo(CoffeeJob.Status.FAILED)
        );
    }

    @TestConfiguration
    static class Configuration {

        @Bean
        @Primary
        CoffeeMachineClient testCoffeeMachineClient() {
            return new TestCoffeeMachineClient();
        }

        @Bean
        @Primary
        ThreadSleeper testCoffeeJobThreadSleeper() {
            return () -> {};
        }
    }

    @NullMarked
    static final class TestCoffeeMachineClient implements CoffeeMachineClient {

        private final Queue<Supplier<MachineCoffeeProgress>> responses = new ArrayDeque<>();

        void progressTo(int progress) {
            responses.add(() -> new MachineCoffeeProgress(
                    new CoffeeType("ESPRESSO"),
                    Progress.of(progress)
            ));
        }

        void fail() {
            responses.add(() -> {
                throw new CoffeeMachineUnavailableException(
                        "machine unavailable",
                        new RuntimeException("boom")
                );
            });
        }

        @Override
        public CoffeeMachineStatus status() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MachineCoffeeProgress progress() {
            return responses.remove().get();
        }

        @Override
        public MachineOrderResult order(CoffeeType type) {
            throw new UnsupportedOperationException();
        }
    }
}
