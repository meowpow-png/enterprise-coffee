package io.github.meowpowpng.enterprisecoffee.coffee.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProgressTest {

    @Test
    void should_ThrowIllegalArgumentException_when_ValueIsLessThanZero() {
        assertThatThrownBy(() -> Progress.of(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ThrowIllegalArgumentException_when_ValueIsGreaterThanOneHundred() {
        assertThatThrownBy(() -> Progress.of(101))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ReturnZero_when_InitialProgressIsCreated() {
        assertThat(Progress.initial().value()).isZero();
    }

    @Test
    void should_ReturnValue_when_ProgressIsCreatedFromValue() {
        var value = 42;

        var progress = Progress.of(value);

        assertThat(progress.value()).isEqualTo(value);
    }

    @Test
    void should_BeEqual_when_ValuesAreEqual() {
        var expected = Progress.of(42);

        assertThat(Progress.of(42))
                .isEqualTo(expected)
                .hasSameHashCodeAs(expected);
    }
}
