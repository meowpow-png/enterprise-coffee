package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineCoffeeProgress;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.TestCoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeTestFixtures;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.support.LoggingTestFixtures;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class CoffeeJobTrackerTest {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);
    
    @Mock
    private CoffeeMachineClient client;

    @Mock
    private DomainEventPublisher publisher;

    @Mock
    private ThreadSleeper sleeper;

    @Nested
    @DisplayName("constructor")
    class ConstructorTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when client is null")
        void should_ThrowNullPointerException_when_ClientIsNull() {
            var thrown = catchThrowable(() -> new CoffeeJobTracker(
                    null,
                    publisher,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when publisher is null")
        void should_ThrowNullPointerException_when_PublisherIsNull() {
            var thrown = catchThrowable(() -> new CoffeeJobTracker(
                    client,
                    null,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when sleeper is null")
        void should_ThrowNullPointerException_when_SleeperIsNull() {
            var thrown = catchThrowable(() -> new CoffeeJobTracker(
                    client,
                    publisher,
                    null,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when timeout is null")
        void should_ThrowNullPointerException_when_TimeoutIsNull() {
            var thrown = catchThrowable(() -> new CoffeeJobTracker(
                    client,
                    publisher,
                    sleeper,
                    null,
                    TestClock.create()
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when clock is null")
        void should_ThrowNullPointerException_when_ClockIsNull() {
            var thrown = catchThrowable(() -> new CoffeeJobTracker(
                    client,
                    publisher,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    null
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("track")
    class TrackMethodTests {

        @Test
        @DisplayName("Should throw IllegalStateException when job is not pending")
        void should_ThrowIllegalStateException_when_JobIsNotPending() {
            var tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            );
            var job = CoffeeJob.restore(
                    CoffeeJob.Id.generate(),
                    CoffeeOrder.Id.generate(),
                    CoffeeJob.Status.IN_PROGRESS,
                    0
            );
            assertThatThrownBy(() -> tracker.track(job))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Should complete job when progress reaches 100 percent")
        void should_CompleteJob_when_ProgressReaches100Percent() {
            var tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            );
            var job = CoffeeJobTestFixtures.validCoffeeJob();

            var progress = new MachineCoffeeProgress(
                    CoffeeTestFixtures.validCoffeeType(),
                    Progress.of(100)
            );
            Mockito.when(client.progress()).thenReturn(progress);

            tracker.track(job);

            assertThat(job.status()).isEqualTo(CoffeeJob.Status.COMPLETED);
        }

        @Test
        @DisplayName("Should fail job when communication with coffee machine fails")
        void should_FailJob_when_CommunicationWithCoffeeMachineFails() {
            var tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            );
            var job = CoffeeJobTestFixtures.validCoffeeJob();

            Mockito.when(client.progress()).thenThrow(
                    new TestCoffeeMachineException("Communication failed")
            );
            LoggingTestFixtures.withoutLogging(CoffeeJobTracker.class, () ->
                tracker.track(job)
            );
            assertThat(job.status()).isEqualTo(CoffeeJob.Status.FAILED);
        }

        @Test
        @DisplayName("Should fail job when tracking is interrupted")
        void should_FailJob_when_TrackingIsInterrupted() {
            var tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            );
            var job = CoffeeJobTestFixtures.validCoffeeJob();

            var progress = new MachineCoffeeProgress(
                    CoffeeTestFixtures.validCoffeeType(),
                    Progress.of(42)
            );
            Mockito.when(client.progress()).thenReturn(progress);
            Mockito.doThrow(new IllegalStateException())
                    .when(sleeper)
                    .sleep();

            tracker.track(job);

            assertThat(job.status()).isEqualTo(CoffeeJob.Status.FAILED);
        }

        @Test
        @DisplayName("Should fail job when tracking times out")
        void should_FailJob_when_TrackingTimesOut() {
            var clock = Mockito.mock(Clock.class);
            var tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    sleeper,
                    DEFAULT_TIMEOUT,
                    clock
            );
            var job = CoffeeJobTestFixtures.validCoffeeJob();

            var now = Instant.now();
            Mockito.when(clock.instant())
                    .thenReturn(now)
                    .thenReturn(now.plusSeconds(30));

            tracker.track(job);

            assertThat(job.status()).isEqualTo(CoffeeJob.Status.FAILED);
        }
    }
}
