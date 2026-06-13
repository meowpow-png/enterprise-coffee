package io.github.meowpowpng.enterprisecoffee.infra;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageResponseTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void should_ThrowNullPointerException_when_MessageIsNull() {
        assertThatThrownBy(() -> new MessageResponse(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_ReturnMessage_when_MessageIsProvided() {
        var message = "message";

        var response = new MessageResponse(message);
        assertThat(response.message()).isEqualTo(message);
    }
}
