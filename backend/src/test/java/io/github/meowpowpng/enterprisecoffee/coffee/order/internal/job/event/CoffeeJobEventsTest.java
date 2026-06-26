package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.event;

import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.TestCoffeeJob.validCoffeeJob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

class CoffeeJobEventsTest {

    @Nested
    @DisplayName("started")
    class StartedEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when job is null")
        void should_ThrowNullPointerException_when_JobIsNull() {
            assertThatThrownBy(() -> CoffeeJobEvents.started(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return started event when job is provided")
        void should_ReturnStartedEvent_when_JobIsProvided() {
            var job = validCoffeeJob();
            var event = CoffeeJobEvents.started(job);

            assertThat(event.job()).isEqualTo(job);
        }
    }

    @Nested
    @DisplayName("progressUpdated")
    class ProgressUpdatedEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when job is null")
        void should_ThrowNullPointerException_when_JobIsNull() {
            var thrown = catchThrowable(() -> CoffeeJobEvents.progressUpdated(
                    null,
                    Progress.of(10)
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when previous is null")
        void should_ThrowNullPointerException_when_PreviousIsNull() {
            var thrown = catchThrowable(() -> CoffeeJobEvents.progressUpdated(
                    validCoffeeJob(),
                    null
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return progress updated event when values are provided")
        void should_ReturnProgressUpdatedEvent_when_ValuesAreProvided() {
            var job = validCoffeeJob();
            var previous = Progress.of(10);
            var event = CoffeeJobEvents.progressUpdated(job, previous);

            assertThat(event.job()).isEqualTo(job);
            assertThat(event.previous()).isEqualTo(previous);
        }
    }

    @Nested
    @DisplayName("finished")
    class FinishedEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when job is null")
        void should_ThrowNullPointerException_when_JobIsNull() {
            assertThatThrownBy(() -> CoffeeJobEvents.finished(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return finished event when job is provided")
        void should_ReturnFinishedEvent_when_JobIsProvided() {
            var job = validCoffeeJob();
            var event = CoffeeJobEvents.finished(job);

            assertThat(event.job()).isEqualTo(job);
        }
    }
}
