package io.github.meowpowpng.enterprisecoffee.coffee.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeTypeTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when value is null")
    void should_ThrowNullPointerException_when_ValueIsNull() {
        assertThatThrownBy(() -> new CoffeeType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when value is blank")
    void should_ThrowIllegalArgumentException_when_ValueIsBlank() {
        assertThatThrownBy(() -> new CoffeeType(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should return value when value is provided")
    void should_ReturnValue_when_ValueIsProvided() {
        var value = "espresso";

        var coffeeType = new CoffeeType(value);
        assertThat(coffeeType.value()).isEqualTo(value);
    }
}
