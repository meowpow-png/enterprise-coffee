package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CoffeeOrderResponseTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when message is null")
    void should_ThrowNullPointerException_when_MessageIsNull() {
        assertThatThrownBy(() -> new CoffeeOrderResponse(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should return message when message is provided")
    void should_ReturnMessage_when_MessageIsProvided() {
        var message = "Coffee order accepted";

        var response = new CoffeeOrderResponse(message);

        assertThat(response.message()).isEqualTo(message);
    }
}
