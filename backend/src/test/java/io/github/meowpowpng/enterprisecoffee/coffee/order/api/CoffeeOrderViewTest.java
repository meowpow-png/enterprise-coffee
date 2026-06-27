package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CoffeeOrderViewTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when id is null")
    void should_ThrowNullPointerException_when_IdIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderView(
                null,
                "ESPRESSO",
                "PENDING",
                Instant.now()
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when type is null")
    void should_ThrowNullPointerException_when_TypeIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderView(
                UUID.randomUUID(),
                null,
                "PENDING",
                Instant.now()
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when status is null")
    void should_ThrowNullPointerException_when_StatusIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderView(
                UUID.randomUUID(),
                "ESPRESSO",
                null,
                Instant.now()
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when createdAt is null")
    void should_ThrowNullPointerException_when_CreatedAtIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderView(
                UUID.randomUUID(),
                "ESPRESSO",
                "PENDING",
                null
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }
}
