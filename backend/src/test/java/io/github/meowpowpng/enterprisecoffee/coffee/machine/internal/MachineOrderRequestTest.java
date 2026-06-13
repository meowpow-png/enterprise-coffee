package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MachineOrderRequestTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void should_ThrowNullPointerException_when_TypeIsNull() {
        assertThatThrownBy(() -> new MachineOrderRequest(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_ReturnType_when_TypeIsProvided() {
        var type = "espresso";

        var request = new MachineOrderRequest(type);

        assertThat(request.type()).isEqualTo(type);
    }
}
