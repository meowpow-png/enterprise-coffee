package io.github.meowpowpng.enterprisecoffee.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageResponseTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when message is null")
    void should_ThrowNullPointerException_when_MessageIsNull() {
        assertThatThrownBy(() -> new MessageResponse(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should return message when message is provided")
    void should_ReturnMessage_when_MessageIsProvided() {
        var message = "message";

        var response = new MessageResponse(message);
        assertThat(response.message()).isEqualTo(message);
    }
}
