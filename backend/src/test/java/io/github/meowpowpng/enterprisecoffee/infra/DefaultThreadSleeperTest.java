package io.github.meowpowpng.enterprisecoffee.infra;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultThreadSleeperTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void should_ThrowNullPointerException_when_IntervalIsNull() {
        assertThatThrownBy(() -> new DefaultThreadSleeper(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_ThrowIllegalArgumentException_when_IntervalIsNotPositive() {
        assertThatThrownBy(() -> new DefaultThreadSleeper(Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_CreateInstance_when_IntervalIsPositive() {
        assertThatCode(() -> new DefaultThreadSleeper(Duration.ofMillis(1)))
                .doesNotThrowAnyException();
    }
}
