package io.github.meowpowpng.enterprisecoffee.coffee.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProgressTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when value is less than zero")
    void should_ThrowIllegalArgumentException_when_ValueIsLessThanZero() {
        assertThatThrownBy(() -> Progress.of(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when value is greater than one hundred")
    void should_ThrowIllegalArgumentException_when_ValueIsGreaterThanOneHundred() {
        assertThatThrownBy(() -> Progress.of(101))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should return zero when initial progress is created")
    void should_ReturnZero_when_InitialProgressIsCreated() {
        assertThat(Progress.initial().value()).isZero();
    }

    @Test
    @DisplayName("Should return value when progress is created from value")
    void should_ReturnValue_when_ProgressIsCreatedFromValue() {
        var value = 42;

        var progress = Progress.of(value);

        assertThat(progress.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("Should be equal when values are equal")
    void should_BeEqual_when_ValuesAreEqual() {
        var expected = Progress.of(42);

        assertThat(Progress.of(42))
                .isEqualTo(expected)
                .hasSameHashCodeAs(expected);
    }
}
