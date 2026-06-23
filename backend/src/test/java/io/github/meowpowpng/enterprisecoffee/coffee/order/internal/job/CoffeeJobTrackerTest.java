package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineCoffeeProgress;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.TestCoffeeMachineException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeTestFixtures;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.event.CoffeeJobEvents;
import io.github.meowpowpng.enterprisecoffee.common.DomainEvent;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.common.ThreadSleeper;
import io.github.meowpowpng.enterprisecoffee.support.LoggingTestFixtures;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.jspecify.annotations.NullMarked;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class CoffeeJobTrackerTest {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);

    @Mock
    private CoffeeMachineClient client;

    private CoffeeJobTracker tracker;

    private final TestDomainEventPublisher publisher = new TestDomainEventPublisher();
    private final TestThreadSleeper threadSleeper = new TestThreadSleeper();

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
                    threadSleeper,
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
                    threadSleeper,
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
                    threadSleeper,
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
                    threadSleeper,
                    DEFAULT_TIMEOUT,
                    null
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("track")
    class TrackMethodTests {

        @BeforeEach
        void setupTrackMethodTest() {
            tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    threadSleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            );
        }

        @Test
        @DisplayName("Should throw IllegalStateException when job is not pending")
        void should_ThrowIllegalStateException_when_JobIsNotPending() {
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
            var job = CoffeeJobTestFixtures.validCoffeeJob();

            var progress = new MachineCoffeeProgress(
                    CoffeeTestFixtures.validCoffeeType(),
                    Progress.of(42)
            );
            Mockito.when(client.progress()).thenReturn(progress);
            threadSleeper.failOnSleep();

            tracker.track(job);

            assertThat(job.status()).isEqualTo(CoffeeJob.Status.FAILED);
        }

        @Test
        @DisplayName("Should fail job when tracking times out")
        void should_FailJob_when_TrackingTimesOut() {
            var clock = TestClock.create();
            var tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    threadSleeper,
                    DEFAULT_TIMEOUT,
                    clock
            );
            var job = CoffeeJobTestFixtures.validCoffeeJob();

            Mockito.when(client.progress()).thenAnswer(invocation -> {
                clock.advance(DEFAULT_TIMEOUT.plusSeconds(1));

                return new MachineCoffeeProgress(
                        CoffeeTestFixtures.validCoffeeType(),
                        Progress.of(42)
                );
            });
            tracker.track(job);
            assertThat(job.status()).isEqualTo(CoffeeJob.Status.FAILED);
        }
    }

    @Nested
    @DisplayName("progress")
    class ProgressTests {

        @BeforeEach
        void setupProgressTest() {
            threadSleeper.failOnSleep();
            tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    threadSleeper,
                    DEFAULT_TIMEOUT,
                    TestClock.create()
            );
        }

        @Test
        @DisplayName("Should update job progress when machine reports increased progress")
        void should_UpdateJobProgress_when_MachineReportsIncreasedProgress() {
            var type = new CoffeeType("ESPRESSO");
            var job = createJob(type);
            var progress = Progress.of(50);

            Mockito.when(client.progress()).thenReturn(
                    new MachineCoffeeProgress(type, progress)
            );
            tracker.track(job);

            assertThat(job.progress()).isEqualTo(progress);
        }

        @Test
        @DisplayName("Should not update job progress when machine reports unchanged progress")
        void should_NotUpdateJobProgress_when_MachineReportsUnchangedProgress() {
            var type = new CoffeeType("ESPRESSO");
            var job = createJob(type);
            var progress = Progress.initial();

            Mockito.when(client.progress()).thenReturn(
                    new MachineCoffeeProgress(type, progress)
            );
            tracker.track(job);

            assertThat(job.progress()).isEqualTo(progress);
        }
    }

    @Nested
    @DisplayName("events")
    class EventTests {

        private TestClock clock;

        @BeforeEach
        void setupEventTest() {
            clock = TestClock.create();
            tracker = new CoffeeJobTracker(
                    client,
                    publisher,
                    threadSleeper,
                    DEFAULT_TIMEOUT,
                    clock
            );
        }

        @Test
        @DisplayName("Should publish started event when job tracking begins")
        void should_PublishStartedEvent_when_JobTrackingBegins() {
            var type = new CoffeeType("ESPRESSO");
            var progress = progressCompleted(type);
            var job = createJob(type);

            Mockito.when(client.progress()).thenReturn(progress);

            tracker.track(job);

            assertThat(publisher.events().getFirst())
                    .isEqualTo(CoffeeJobEvents.started(job));
        }

        @Test
        @DisplayName("Should publish finished event when job completes")
        void should_PublishFinishedEvent_when_JobCompletes() {
            var type = new CoffeeType("ESPRESSO");
            var progress = progressCompleted(type);
            var job = createJob(type);

            Mockito.when(client.progress()).thenReturn(progress);

            tracker.track(job);

            assertThat(publisher.events().getLast())
                    .isEqualTo(CoffeeJobEvents.finished(job));
        }

        @Test
        @DisplayName("Should publish progress updated event when job progress increases")
        void should_PublishProgressUpdatedEvent_when_JobProgressIncreases() {
            var type = new CoffeeType("ESPRESSO");
            var job = createJob(type);
            var progress = Progress.of(50);

            Mockito.when(client.progress()).thenReturn(
                    new MachineCoffeeProgress(type, progress)
            );
            threadSleeper.failOnSleep();
            tracker.track(job);

            var expected = CoffeeJobEvents.progressUpdated(
                    job,
                    Progress.initial()
            );
            assertThat(publisher.events()).contains(expected);
        }

        @Test
        @DisplayName("Should not publish progress updated event when job progress is unchanged")
        void should_NotPublishProgressUpdatedEvent_when_JobProgressIsUnchanged() {
            var type = new CoffeeType("ESPRESSO");
            var job = createJob(type);
            var progress = Progress.initial();

            Mockito.when(client.progress()).thenReturn(
                    new MachineCoffeeProgress(type, progress)
            );
            threadSleeper.failOnSleep();
            tracker.track(job);

            assertThat(publisher.events())
                    .filteredOn(CoffeeJobEvents.ProgressUpdated.class::isInstance)
                    .isEmpty();
        }

        @Test
        @DisplayName("Should publish finished event when job fails due to communication failure")
        void should_PublishFinishedEvent_when_JobFailsDueToCommunicationFailure() {
            var type = new CoffeeType("ESPRESSO");
            var job = createJob(type);

            var exception = new CoffeeMachineUnavailableException(
                    "Machine unavailable",
                    new RuntimeException()
            );
            Mockito.when(client.progress()).thenThrow(exception);

            tracker.track(job);

            assertThat(publisher.events().getLast())
                    .isEqualTo(CoffeeJobEvents.finished(job));
        }

        @Test
        @DisplayName("Should publish finished event when job fails due to interruption")
        void should_PublishFinishedEvent_when_JobFailsDueToInterruption() {
            var type = new CoffeeType("ESPRESSO");
            var job = createJob(type);
            var progress = new MachineCoffeeProgress(
                    type,
                    Progress.of(50)
            );
            Mockito.when(client.progress()).thenReturn(progress);
            threadSleeper.failOnSleep();

            tracker.track(job);

            assertThat(publisher.events().getLast())
                    .isEqualTo(CoffeeJobEvents.finished(job));
        }

        @Test
        @DisplayName("Should publish finished event when job times out")
        void should_PublishFinishedEvent_when_JobTimesOut() {
            var type = new CoffeeType("ESPRESSO");
            var job = createJob(type);

            Mockito.when(client.progress()).thenAnswer(invocation -> {
                clock.advance(DEFAULT_TIMEOUT.plusSeconds(1));

                var progress = Progress.of(50);
                return new MachineCoffeeProgress(type, progress);
            });
            tracker.track(job);

            assertThat(publisher.events().getLast())
                    .isEqualTo(CoffeeJobEvents.finished(job));
        }
    }

    private static CoffeeJob createJob(CoffeeType type) {
        var order = CoffeeOrder.create(type);
        return CoffeeJob.create(order.id());
    }

    private static MachineCoffeeProgress progressCompleted(CoffeeType type) {
        return new MachineCoffeeProgress(type, Progress.of(100));
    }

    private static final class TestThreadSleeper implements ThreadSleeper {

        private boolean fail;

        public void failOnSleep() {
            this.fail = true;
        }

        @Override
        public void sleep() {
            if (fail) {
                throw new IllegalStateException();
            }
        }
    }

    @NullMarked
    private static final class TestDomainEventPublisher implements DomainEventPublisher {

        private final List<DomainEvent> events = new ArrayList<>();

        @Override
        public void publish(DomainEvent event) {
            events.add(event);
        }

        public List<DomainEvent> events() {
            return events;
        }
    }
}
