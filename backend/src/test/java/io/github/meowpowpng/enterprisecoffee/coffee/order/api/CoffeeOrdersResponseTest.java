package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeOrdersResponseTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when orders is null")
    void should_ThrowNullPointerException_when_OrdersIsNull() {
        assertThatThrownBy(() -> new CoffeeOrdersResponse(null))
                .isInstanceOf(NullPointerException.class);
    }
}
