package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MachineProgressResponseTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when type is null")
    void should_ThrowNullPointerException_when_TypeIsNull() {
        assertThatThrownBy(() -> new MachineProgressResponse(null, 42))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should return values when values are provided")
    void should_ReturnValues_when_ValuesAreProvided() {
        var type = "espresso";
        var progress = 42;

        var response = new MachineProgressResponse(type, progress);

        assertThat(response.type()).isEqualTo(type);
        assertThat(response.progress()).isEqualTo(progress);
    }
}
