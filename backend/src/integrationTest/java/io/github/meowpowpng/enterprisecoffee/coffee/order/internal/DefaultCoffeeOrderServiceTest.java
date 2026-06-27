package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderRequest;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderView;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.support.DisableAsync;
import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockWebServerTest;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import org.awaitility.Durations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisableAsync
@Transactional
@SpringBootTest
@IntegrationTest
@Import(CoffeeOrderServiceConfiguration.class)
class DefaultCoffeeOrderServiceTest extends MockWebServerTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("coffee.machine.base-url", () -> server.url("/").toString());
    }

    @Autowired
    private CoffeeOrderService service;

    @Autowired
    private JpaCoffeeOrderCrudRepository orderRepository;

    @Nested
    @DisplayName("order")
    class OrderMethodTests {

        @Test
        @DisplayName("Should return accepted response when machine accepts order")
        void should_ReturnAcceptedResponse_when_MachineAcceptsOrder() {
            server.enqueue(new MockResponse().setResponseCode(202));

            var request = TestCoffeeOrderRequest.create();
            var response = service.order(request);

            assertThat(response).isEqualTo(CoffeeOrderResponse.accepted());
        }

        @Test
        @DisplayName("Should persist accepted order when machine accepts order")
        void should_PersistAcceptedOrder_when_MachineAcceptsOrder() {
            server.enqueue(new MockResponse().setResponseCode(202));

            var coffeeType = "ESPRESSO";
            var request = new CoffeeOrderRequest(coffeeType);

            service.order(request);

            var orders = orderRepository.findAll();

            assertThat(orders).hasSize(1);

            var order = orders.getFirst();

            assertThat(order.getType()).isEqualTo(coffeeType);
            assertThat(order.getStatus()).isEqualTo(CoffeeOrder.Status.ACCEPTED);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderProcessingException when machine is busy")
        void should_ThrowCoffeeOrderProcessingException_when_MachineIsBusy() {
            server.enqueue(new MockResponse().setResponseCode(409));

            var request = TestCoffeeOrderRequest.create();

            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderProcessingException.class);
        }

        @Test
        @DisplayName("Should persist rejected order when machine is busy")
        void should_PersistRejectedOrder_when_MachineIsBusy() {
            server.enqueue(new MockResponse().setResponseCode(409));

            var coffeeType = "ESPRESSO";
            var request = new CoffeeOrderRequest(coffeeType);

            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderProcessingException.class);

            var orders = orderRepository.findAll();

            assertThat(orders).hasSize(1);

            var order = orders.getFirst();

            assertThat(order.getType()).isEqualTo(coffeeType);
            assertThat(order.getStatus()).isEqualTo(CoffeeOrder.Status.REJECTED);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderInvalidException when machine rejects order")
        void should_ThrowCoffeeOrderInvalidException_when_MachineRejectsOrder() {
            server.enqueue(new MockResponse().setResponseCode(400));

            var request = TestCoffeeOrderRequest.create();

            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderInvalidException.class);
        }

        @Test
        @DisplayName("Should persist invalid order when machine rejects order")
        void should_PersistInvalidOrder_when_MachineRejectsOrder() {
            server.enqueue(new MockResponse().setResponseCode(400));

            var coffeeType = "ESPRESSO";
            var request = new CoffeeOrderRequest(coffeeType);

            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderInvalidException.class);

            var orders = orderRepository.findAll();

            assertThat(orders).hasSize(1);

            var order = orders.getFirst();

            assertThat(order.getType()).isEqualTo(coffeeType);
            assertThat(order.getStatus()).isEqualTo(CoffeeOrder.Status.INVALID);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderProcessingException when machine is unavailable")
        void should_ThrowCoffeeOrderProcessingException_when_MachineIsUnavailable() {
            server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));

            var request = new CoffeeOrderRequest("ESPRESSO");

            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderProcessingException.class);
        }

        @Test
        @DisplayName("Should persist failed order when machine is unavailable")
        void should_PersistFailedOrder_when_MachineIsUnavailable() {
            server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));

            var coffeeType = "ESPRESSO";
            var request = new CoffeeOrderRequest(coffeeType);

            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderProcessingException.class);

            var orders = orderRepository.findAll();

            assertThat(orders).hasSize(1);

            var order = orders.getFirst();

            assertThat(order.getType()).isEqualTo(coffeeType);
            assertThat(order.getStatus()).isEqualTo(CoffeeOrder.Status.FAILED);
        }
    }

    @Nested
    @DisplayName("findLatest")
    class FindLatestMethodTests {

        @Autowired
        private CoffeeOrderRepository repository;

        @Test
        @DisplayName("Should return latest coffee orders from newest to oldest")
        void should_ReturnLatestCoffeeOrdersFromNewestToOldest_when_OrdersExist() {
            var clock = TestClock.create(Instant.parse("2025-01-01T10:00:00Z"));
            var oldest = TestCoffeeOrder.create(
                    "ESPRESSO",
                    clock.instant()
            );
            var newest = TestCoffeeOrder.create(
                    "LATTE",
                    clock.advance(Durations.ONE_SECOND)
            );
            repository.save(oldest);
            repository.save(newest);

            var response = service.findLatest(10);

            assertThat(response.orders())
                    .extracting(CoffeeOrderView::type)
                    .containsExactly("LATTE", "ESPRESSO");
        }

        @Test
        @DisplayName("Should return requested number of latest coffee orders")
        void should_ReturnRequestedNumberOfLatestCoffeeOrders_when_LimitIsSpecified() {
            var clock = TestClock.create(Instant.parse("2025-01-01T10:00:00Z"));
            repository.save(TestCoffeeOrder.create(
                    "ESPRESSO",
                    clock.instant()
            ));
            repository.save(TestCoffeeOrder.create(
                    "LATTE",
                    clock.advance(Durations.ONE_SECOND)
            ));
            repository.save(TestCoffeeOrder.create(
                    "CAPPUCCINO",
                    clock.advance(Durations.ONE_SECOND)
            ));
            var response = service.findLatest(2);

            assertThat(response.orders())
                    .extracting(CoffeeOrderView::type)
                    .containsExactly("CAPPUCCINO", "LATTE");
        }
    }
}
