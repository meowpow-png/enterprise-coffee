package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.TestCoffeeType;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeOrderFactoryTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when clock is null")
    void should_ThrowNullPointerException_when_ClockIsNull() {
        assertThatThrownBy(() -> new CoffeeOrderFactory(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Should throw NullPointerException when type is null")
    void should_ThrowNullPointerException_when_TypeIsNull() {
        var clock = TestClock.create();
        var factory = new CoffeeOrderFactory(clock);

        assertThatThrownBy(() -> factory.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Creates coffee order with current time when type is valid")
    void should_CreateCoffeeOrderWithCurrentTime_when_TypeIsValid() {
        var createdAt = Instant.parse("2025-01-01T12:00:00Z");
        var clock = TestClock.create(createdAt);
        var factory = new CoffeeOrderFactory(clock);
        var type = TestCoffeeType.create();

        var order = factory.create(type);

        assertThat(order.createdAt()).isEqualTo(createdAt);
    }
}
