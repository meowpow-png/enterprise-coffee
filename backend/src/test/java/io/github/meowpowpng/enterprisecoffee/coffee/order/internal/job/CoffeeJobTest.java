package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobTestFixtures.validCoffeeJob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

class CoffeeJobTest {

    @Nested
    @DisplayName("create")
    class CreateMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order id is null")
        void should_ThrowNullPointerException_when_OrderIdIsNull() {
            assertThatThrownBy(() -> CoffeeJob.create(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should initialize progress when coffee job is created")
        void should_InitializeProgress_when_CoffeeJobIsCreated() {
            assertThat(validCoffeeJob().progress()).isEqualTo(Progress.initial());
        }

        @Test
        @DisplayName("Should start in pending state when coffee job is created")
        void should_StartInPendingState_when_CoffeeJobIsCreated() {
            assertThat(validCoffeeJob().status()).isEqualTo(CoffeeJob.Status.PENDING);
        }

        @Test
        @DisplayName("Should preserve order id when coffee job is created")
        void should_PreserveOrderId_when_CoffeeJobIsCreated() {
            var orderId = CoffeeOrder.Id.generate();

            var result = CoffeeJob.create(orderId);

            assertThat(result.orderId()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("Should generate identifier when coffee job is created")
        void should_GenerateIdentifier_when_CoffeeJobIsCreated() {
            assertThat(validCoffeeJob().id()).isNotNull();
        }
    }

    @Nested
    @DisplayName("restore")
    class RestoreMethodTests {

        @Test
        @DisplayName("Should restore coffee job when valid state is provided")
        void should_RestoreCoffeeJob_when_ValidStateIsProvided() {
            var id = CoffeeJob.Id.generate();
            var orderId = CoffeeOrder.Id.generate();
            var progress = Progress.of(42);

            var result = CoffeeJob.restore(
                    id,
                    orderId,
                    CoffeeJob.Status.IN_PROGRESS,
                    progress.value()
            );
            assertThat(result.id()).isEqualTo(id);
            assertThat(result.orderId()).isEqualTo(orderId);
            assertThat(result.status()).isEqualTo(CoffeeJob.Status.IN_PROGRESS);
            assertThat(result.progress()).isEqualTo(progress);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when id is null")
        void should_ThrowNullPointerException_when_IdIsNull() {
            var thrown = catchThrowable(() -> CoffeeJob.restore(
                    null,
                    CoffeeOrder.Id.generate(),
                    CoffeeJob.Status.PENDING,
                    0
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order id is null")
        void should_ThrowNullPointerException_when_OrderIdIsNull() {
            var thrown = catchThrowable(() -> CoffeeJob.restore(
                    CoffeeJob.Id.generate(),
                    null,
                    CoffeeJob.Status.PENDING,
                    0
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when status is null")
        void should_ThrowNullPointerException_when_StatusIsNull() {
            var thrown = catchThrowable(() -> CoffeeJob.restore(
                    CoffeeJob.Id.generate(),
                    CoffeeOrder.Id.generate(),
                    null,
                    0
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when progress is outside valid range")
        void should_ThrowIllegalArgumentException_when_ProgressIsOutsideValidRange() {
            var thrown = catchThrowable(() -> CoffeeJob.restore(
                    CoffeeJob.Id.generate(),
                    CoffeeOrder.Id.generate(),
                    CoffeeJob.Status.PENDING,
                    -1
            ));
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("start")
    class StartMethodTests {

        @Test
        @DisplayName("Should mark job as started when job is pending")
        void should_MarkJobAsStarted_when_JobIsPending() {
            var job = validCoffeeJob();

            job.start();

            assertThat(job.status()).isEqualTo(CoffeeJob.Status.IN_PROGRESS);
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
            assertThatThrownBy(job::start)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("updateProgress")
    class UpdateProgressMethodTests {

        @Test
        @DisplayName("Should update current progress when job is in progress")
        void should_UpdateCurrentProgress_when_JobIsInProgress() {
            var job = validCoffeeJob();
            var progress = Progress.of(42);

            job.start();
            job.updateProgress(progress);

            assertThat(job.progress()).isEqualTo(progress);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when job is not in progress")
        void should_ThrowIllegalStateException_when_JobIsNotInProgress() {
            var job = validCoffeeJob();
            var progress = Progress.of(42);

            assertThatThrownBy(() -> job.updateProgress(progress))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when progress decreases")
        void should_ThrowIllegalArgumentException_when_ProgressDecreases() {
            var job = validCoffeeJob();

            job.start();
            job.updateProgress(Progress.of(42));

            assertThatThrownBy(() -> job.updateProgress(Progress.of(41)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("complete")
    class CompleteMethodTests {

        @Test
        @DisplayName("Should complete job when job is in progress")
        void should_CompleteJob_when_JobIsInProgress() {
            var job = validCoffeeJob();

            job.start();
            job.complete();

            assertThat(job.status()).isEqualTo(CoffeeJob.Status.COMPLETED);
        }

        @Test
        @DisplayName("Should update progress to 100 percent when job is completed")
        void should_UpdateProgressTo100Percent_when_JobIsCompleted() {
            var job = validCoffeeJob();

            job.start();
            job.complete();

            var expected = Progress.of(100);
            assertThat(job.progress()).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when job is not in progress")
        void should_ThrowIllegalStateException_when_JobIsNotInProgress() {
            assertThatThrownBy(() -> validCoffeeJob().complete())
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("fail")
    class FailMethodTests {

        @Test
        @DisplayName("Should mark job as failed when job can be failed")
        void should_MarkJobAsFailed_when_JobCanBeFailed() {
            var job = validCoffeeJob();

            job.fail();

            var expected = CoffeeJob.Status.FAILED;
            assertThat(job.status()).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when job has already completed or failed")
        void should_ThrowIllegalStateException_when_JobHasAlreadyCompletedOrFailed() {
            var job = validCoffeeJob();

            job.fail();

            assertThatThrownBy(job::fail)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("Id")
    class IdTests {

        @Test
        @DisplayName("Should create identifier when generate method is invoked")
        void should_CreateIdentifier_when_GenerateMethodIsInvoked() {
            assertThat(CoffeeJob.Id.generate()).isNotNull();
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when value is null")
        void should_ThrowNullPointerException_when_ValueIsNull() {
            assertThatThrownBy(() -> new CoffeeJob.Id(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
