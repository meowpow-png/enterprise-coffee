package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeTestFixtures.validCoffeeType;
import static io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrderTestFixtures.validCoffeeOrder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

class CoffeeOrderTest {

    private static final TestClock CLOCK = TestClock.create();

    @Nested
    @DisplayName("create")
    class CreateMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when type is null")
        void should_ThrowNullPointerException_when_TypeIsNull() {
            assertThatThrownBy(() -> CoffeeOrder.create(null, CLOCK.instant()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should create pending order when type is provided")
        void should_CreatePendingOrder_when_TypeIsProvided() {
            var type = validCoffeeType();

            var order = CoffeeOrder.create(type, CLOCK.instant());

            assertThat(order.status()).isEqualTo(CoffeeOrder.Status.PENDING);
            assertThat(order.type()).isEqualTo(type);
        }

        @Test
        @DisplayName("Should create order with identifier when type is provided")
        void should_CreateOrderWithIdentifier_when_TypeIsProvided() {
            var order = CoffeeOrder.create(validCoffeeType(), CLOCK.instant());

            assertThat(order.id()).isNotNull();
        }
    }

    @Nested
    @DisplayName("restore")
    class RestoreMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when id is null")
        void should_ThrowNullPointerException_when_IdIsNull() {
            var thrown = catchThrowable(() -> CoffeeOrder.restore(
                    null,
                    validCoffeeType(),
                    CoffeeOrder.Status.PENDING,
                    CLOCK.instant()
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when type is null")
        void should_ThrowNullPointerException_when_TypeIsNull() {
            var thrown = catchThrowable(() -> CoffeeOrder.restore(
                    CoffeeOrder.Id.generate(),
                    null,
                    CoffeeOrder.Status.PENDING,
                    CLOCK.instant()
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when status is null")
        void should_ThrowNullPointerException_when_StatusIsNull() {
            var thrown = catchThrowable(() -> CoffeeOrder.restore(
                    CoffeeOrder.Id.generate(),
                    validCoffeeType(),
                    null,
                    CLOCK.instant()
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when createdAt is null")
        void should_ThrowNullPointerException_when_CreatedAtIsNull() {
            var thrown = catchThrowable(() -> CoffeeOrder.restore(
                    CoffeeOrder.Id.generate(),
                    validCoffeeType(),
                    CoffeeOrder.Status.PENDING,
                    null
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should restore order when values are provided")
        void should_RestoreOrder_when_ValuesAreProvided() {
            var id = CoffeeOrder.Id.generate();
            var type = new CoffeeType("ESPRESSO");
            var status = CoffeeOrder.Status.ACCEPTED;
            var createdAt = CLOCK.instant();

            var order = CoffeeOrder.restore(id, type, status, createdAt);

            assertThat(order.id()).isEqualTo(id);
            assertThat(order.type()).isEqualTo(type);
            assertThat(order.status()).isEqualTo(status);
            assertThat(order.createdAt()).isEqualTo(createdAt);
        }
    }

    @Nested
    @DisplayName("accept")
    class AcceptMethodTests {

        @Test
        @DisplayName("Should return accepted order when order is accepted")
        void should_ReturnAcceptedOrder_when_OrderIsAccepted() {
            var order = validCoffeeOrder();
            var acceptedOrder = order.accept();

            assertThat(acceptedOrder.id()).isEqualTo(order.id());
            assertThat(acceptedOrder.type()).isEqualTo(order.type());
            assertThat(acceptedOrder.status()).isEqualTo(CoffeeOrder.Status.ACCEPTED);
        }
    }

    @Nested
    @DisplayName("reject")
    class RejectMethodTests {

        @Test
        @DisplayName("Should return rejected order when order is rejected")
        void should_ReturnRejectedOrder_when_OrderIsRejected() {
            var order = validCoffeeOrder();
            var rejectedOrder = order.reject();

            assertThat(rejectedOrder.id()).isEqualTo(order.id());
            assertThat(rejectedOrder.type()).isEqualTo(order.type());
            assertThat(rejectedOrder.status()).isEqualTo(CoffeeOrder.Status.REJECTED);
        }
    }

    @Nested
    @DisplayName("markInvalid")
    class MarkInvalidMethodTests {

        @Test
        @DisplayName("Should return invalid order when order is marked invalid")
        void should_ReturnInvalidOrder_when_OrderIsMarkedInvalid() {
            var order = validCoffeeOrder();
            var invalidOrder = order.markInvalid();

            assertThat(invalidOrder.id()).isEqualTo(order.id());
            assertThat(invalidOrder.type()).isEqualTo(order.type());
            assertThat(invalidOrder.status()).isEqualTo(CoffeeOrder.Status.INVALID);
        }
    }

    @Nested
    @DisplayName("fail")
    class FailMethodTests {

        @Test
        @DisplayName("Should return failed order when order fails")
        void should_ReturnFailedOrder_when_OrderFails() {
            var order = validCoffeeOrder();
            var failedOrder = order.fail();

            assertThat(failedOrder.id()).isEqualTo(order.id());
            assertThat(failedOrder.type()).isEqualTo(order.type());
            assertThat(failedOrder.status()).isEqualTo(CoffeeOrder.Status.FAILED);
        }
    }

    @Nested
    @DisplayName("id")
    class IdTests {

        @Test
        @DisplayName("Should create identifier when generate identifier method is invoked")
        void should_CreateIdentifier_when_generateIdentifierMethodIsInvoked() {
            assertThat(CoffeeOrder.Id.generate()).isNotNull();
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when value is null")
        void should_ThrowNullPointerException_when_ValueIsNull() {
            assertThatThrownBy(() -> new CoffeeOrder.Id(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return value when value is provided")
        void should_ReturnValue_when_ValueIsProvided() {
            var value = UUID.randomUUID();

            var id = new CoffeeOrder.Id(value);

            assertThat(id.value()).isEqualTo(value);
        }
    }
}
