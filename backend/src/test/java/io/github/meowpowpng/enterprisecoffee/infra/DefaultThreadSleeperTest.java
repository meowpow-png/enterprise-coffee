package io.github.meowpowpng.enterprisecoffee.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

class DefaultThreadSleeperTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when interval is null")
    void should_ThrowNullPointerException_when_IntervalIsNull() {
        assertThatThrownBy(() -> new DefaultThreadSleeper(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when interval is not positive")
    void should_ThrowIllegalArgumentException_when_IntervalIsNotPositive() {
        assertThatThrownBy(() -> new DefaultThreadSleeper(Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should create instance when interval is positive")
    void should_CreateInstance_when_IntervalIsPositive() {
        assertThatCode(() -> new DefaultThreadSleeper(Duration.ofMillis(1)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw IllegalStateException when thread is interrupted")
    void should_ThrowIllegalStateException_when_ThreadIsInterrupted() {

        var sleeper = new DefaultThreadSleeper(Duration.ofMillis(1));
        var interruptFlagRestored = new AtomicBoolean();
        var exceptionThrown = new AtomicBoolean();

        var thread = new Thread(() -> {
            Thread.currentThread().interrupt();
            try {
                sleeper.sleep();
            }
            catch (IllegalStateException e) {
                exceptionThrown.set(true);
                interruptFlagRestored.set(Thread.currentThread().isInterrupted());
            }
        });
        thread.start();

        assertThatCode(thread::join).doesNotThrowAnyException();
        assertThat(exceptionThrown.get()).isTrue();
        assertThat(interruptFlagRestored.get()).isTrue();
    }
}
