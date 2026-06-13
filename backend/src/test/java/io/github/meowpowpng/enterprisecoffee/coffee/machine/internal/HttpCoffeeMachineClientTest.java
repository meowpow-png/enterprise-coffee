package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import org.springframework.web.client.RestClient;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpCoffeeMachineClientTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when RestClient is null")
    void should_ThrowNullPointerException_when_RestClientIsNull() {
        assertThatThrownBy(() -> new HttpCoffeeMachineClient(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when type is null")
    void should_ThrowNullPointerException_when_TypeIsNull() {
        var restClient = Mockito.mock(RestClient.class);
        var machineClient = new HttpCoffeeMachineClient(restClient);

        assertThatThrownBy(() -> machineClient.order(null))
                .isInstanceOf(NullPointerException.class);
    }
}
