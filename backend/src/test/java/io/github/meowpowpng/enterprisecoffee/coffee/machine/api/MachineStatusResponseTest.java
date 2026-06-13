package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MachineStatusResponseTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void should_ThrowNullPointerException_when_StatusIsNull() {
        assertThatThrownBy(() -> new MachineStatusResponse(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_ReturnStatus_when_StatusIsProvided() {
        var status = "READY";

        var response = new MachineStatusResponse(status);

        assertThat(response.status()).isEqualTo(status);
    }
}
