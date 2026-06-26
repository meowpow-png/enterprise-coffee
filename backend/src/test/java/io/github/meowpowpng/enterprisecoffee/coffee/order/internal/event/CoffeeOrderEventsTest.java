package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.meowpowpng.enterprisecoffee.coffee.order.internal.TestCoffeeOrder.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeOrderEventsTest {

    @Nested
    @DisplayName("accepted")
    class AcceptedEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order is null")
        void should_ThrowNullPointerException_when_OrderIsNull() {
            assertThatThrownBy(() -> CoffeeOrderEvents.accepted(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return accepted event when order is provided")
        void should_ReturnAcceptedEvent_when_OrderIsProvided() {
            var order = create();
            var event = CoffeeOrderEvents.accepted(order);

            assertThat(event.order()).isEqualTo(order);
        }
    }

    @Nested
    @DisplayName("stored")
    class StoredEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order is null")
        void should_ThrowNullPointerException_when_OrderIsNull() {
            assertThatThrownBy(() -> CoffeeOrderEvents.store(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return stored event when order is provided")
        void should_ReturnStoredEvent_when_OrderIsProvided() {
            var order = create();
            var event = CoffeeOrderEvents.store(order);

            assertThat(event.order()).isEqualTo(order);
        }
    }

    @Nested
    @DisplayName("rejected")
    class RejectedEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order is null")
        void should_ThrowNullPointerException_when_OrderIsNull() {
            assertThatThrownBy(() -> CoffeeOrderEvents.rejected(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return rejected event when order is provided")
        void should_ReturnRejectedEvent_when_OrderIsProvided() {
            var order = create();
            var event = CoffeeOrderEvents.rejected(order);

            assertThat(event.order()).isEqualTo(order);
        }
    }

    @Nested
    @DisplayName("invalid")
    class InvalidEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order is null")
        void should_ThrowNullPointerException_when_OrderIsNull() {
            assertThatThrownBy(() -> CoffeeOrderEvents.invalid(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return invalid event when order is provided")
        void should_ReturnInvalidEvent_when_OrderIsProvided() {
            var order = create();
            var event = CoffeeOrderEvents.invalid(order);

            assertThat(event.order()).isEqualTo(order);
        }
    }

    @Nested
    @DisplayName("failed")
    class FailedEventTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order is null")
        void should_ThrowNullPointerException_when_OrderIsNull() {
            assertThatThrownBy(() -> CoffeeOrderEvents.failed(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return failed event when order is provided")
        void should_ReturnFailedEvent_when_OrderIsProvided() {
            var order = create();
            var event = CoffeeOrderEvents.failed(order);

            assertThat(event.order()).isEqualTo(order);
        }
    }
}
