package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineClient;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineOrderResult;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.TestCoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.support.LoggingTestFixtures;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@ExtendWith(MockitoExtension.class)
class DefaultCoffeeOrderServiceTest {

    @Mock
    private CoffeeMachineClient client;

    @Mock
    private DomainEventPublisher publisher;

    private CoffeeOrderFactory factory;
    private CoffeeOrderRepository repository;

    @BeforeEach
    void setupDefaultCoffeeOrderServiceTest() {
        this.factory = new CoffeeOrderFactory(TestClock.create());
        this.repository = new JpaCoffeeOrderRepository(
                Mockito.mock(JpaCoffeeOrderCrudRepository.class)
        );
    }

    @Nested
    @DisplayName("constructor")
    class ConstructorTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when client is null")
        void should_ThrowNullPointerException_when_ClientIsNull() {
            var thrown = catchThrowable(() -> new DefaultCoffeeOrderService(
                    null,
                    publisher,
                    factory,
                    repository
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when publisher is null")
        void should_ThrowNullPointerException_when_PublisherIsNull() {
            var thrown = catchThrowable(() -> new DefaultCoffeeOrderService(
                    client,
                    null,
                    factory,
                    repository
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when factory is null")
        void should_ThrowNullPointerException_when_FactoryIsNull() {
            var thrown = catchThrowable(() -> new DefaultCoffeeOrderService(
                    client,
                    publisher,
                    null,
                    repository
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when repository is null")
        void should_ThrowNullPointerException_when_RepositoryIsNull() {
            var thrown = catchThrowable(() -> new DefaultCoffeeOrderService(
                    client,
                    publisher,
                    factory,
                    null
            ));
            assertThat(thrown).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("order")
    class OrderMethodTests {

        private DefaultCoffeeOrderService service;

        @BeforeEach
        void setupOrderMethodTest() {
            service = orderService();
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when request is null")
        void should_ThrowNullPointerException_when_RequestIsNull() {
            assertThatThrownBy(() -> service.order(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return response when order is accepted")
        void should_ReturnResponse_when_OrderIsAccepted() {
            Mockito.when(client.order(TestCoffeeType.create())).thenReturn(
                    MachineOrderResult.ACCEPTED
            );
            var request = TestCoffeeOrderRequest.create();
            var response = service.order(request);

            assertThat(response).isEqualTo(CoffeeOrderResponse.accepted());
        }

        @Test
        @DisplayName("Should throw CoffeeOrderInvalidException when order is invalid")
        void should_ThrowCoffeeOrderInvalidException_when_OrderIsInvalid() {
            Mockito.when(client.order(TestCoffeeType.create())).thenReturn(
                    MachineOrderResult.INVALID
            );
            var request = TestCoffeeOrderRequest.create();
            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderInvalidException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderProcessingException when machine is busy")
        void should_ThrowCoffeeOrderProcessingException_when_MachineIsBusy() {
            Mockito.when(client.order(TestCoffeeType.create())).thenReturn(
                    MachineOrderResult.BUSY
            );
            var request = TestCoffeeOrderRequest.create();
            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderProcessingException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderProcessingException when machine is unavailable")
        void should_ThrowCoffeeOrderProcessingException_when_MachineIsUnavailable() {
            var coffeeType = TestCoffeeType.create();
            var exception = new CoffeeMachineUnavailableException(
                    "machine unavailable",
                    new RuntimeException("boom")
            );
            Mockito.when(client.order(coffeeType)).thenThrow(exception);

            var request = TestCoffeeOrderRequest.create();
            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderProcessingException.class)
                    .hasCause(exception);
        }

        @Test
        @DisplayName("Should propagate CoffeeMachineProtocolException when machine violates protocol")
        void should_PropagateCoffeeMachineProtocolException_when_MachineViolatesProtocol() {
            var coffeeType = TestCoffeeType.create();
            Runnable action = () -> {
                var exception = new CoffeeMachineProtocolException(
                        "protocol violation",
                        new RuntimeException("boom")
                );
                Mockito.when(client.order(coffeeType)).thenThrow(exception);

                var request = TestCoffeeOrderRequest.create();
                assertThatThrownBy(() -> service.order(request)).isSameAs(exception);
            };
            LoggingTestFixtures.withoutLogging(DefaultCoffeeOrderService.class, action);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw IllegalStateException when machine returns unexpected result")
        void should_ThrowIllegalStateException_when_MachineReturnsUnexpectedResult() {
            var coffeeType = TestCoffeeType.create();
            // unexpected result path triggers exception in implementation
            Mockito.when(client.order(coffeeType)).thenReturn(null);

            var request = TestCoffeeOrderRequest.create();
            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("findLatest")
    class FindLatestMethodTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when limit is less than one")
        void should_ThrowIllegalArgumentException_when_LimitIsLessThanOne() {
            assertThatThrownBy(() -> orderService().findLatest(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private DefaultCoffeeOrderService orderService() {
        return new DefaultCoffeeOrderService(
                client,
                publisher,
                factory,
                repository
        );
    }
}
