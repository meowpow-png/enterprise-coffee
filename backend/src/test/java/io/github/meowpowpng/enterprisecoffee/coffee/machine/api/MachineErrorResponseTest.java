package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MachineErrorResponseTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void should_ThrowNullPointerException_when_MessageIsNull() {
        assertThatThrownBy(() -> new MachineErrorResponse(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_ReturnMessage_when_MessageIsProvided() {
        var message = "Machine is unavailable";

        var response = new MachineErrorResponse(message);

        assertThat(response.message()).isEqualTo(message);
    }
}
