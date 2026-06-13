package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MachineCoffeeProgressTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when progress is null")
    void should_ThrowNullPointerException_when_ProgressIsNull() {
        assertThatThrownBy(() -> new MachineCoffeeProgress(null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should return values when progress is provided")
    void should_ReturnValues_when_ProgressIsProvided() {
        var type = new CoffeeType("espresso");
        var progress = Progress.of(42);

        var machineProgress = new MachineCoffeeProgress(
                type,
                progress
        );
        assertThat(machineProgress.type()).isEqualTo(type);
        assertThat(machineProgress.progress()).isEqualTo(progress);
    }
}
