package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderRequest;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderResponse;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.CoffeeOrderService;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobTracker;
import io.github.meowpowpng.enterprisecoffee.support.DisableAsync;
import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockWebServerTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;

import org.junit.jupiter.api.*;

import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisableAsync
@Transactional
@SpringBootTest
@IntegrationTest
@Import(DefaultCoffeeOrderServiceTest.TestCoffeeJobConfiguration.class)
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
    @DisplayName("accepted")
    class AcceptedTests {

        @Test
        @DisplayName("Should return accepted response when machine accepts order")
        void should_ReturnAcceptedResponse_when_MachineAcceptsOrder() {
            server.enqueue(new MockResponse().setResponseCode(202));

            var request = new CoffeeOrderRequest("ESPRESSO");
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
    }

    @Nested
    @DisplayName("busy")
    class BusyTests {

        @Test
        @DisplayName("Should throw CoffeeOrderProcessingException when machine is busy")
        void should_ThrowCoffeeOrderProcessingException_when_MachineIsBusy() {
            server.enqueue(new MockResponse().setResponseCode(409));

            var request = new CoffeeOrderRequest("ESPRESSO");

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
    }

    @Nested
    @DisplayName("invalid")
    class InvalidTests {

        @Test
        @DisplayName("Should throw CoffeeOrderInvalidException when machine rejects order")
        void should_ThrowCoffeeOrderInvalidException_when_MachineRejectsOrder() {
            server.enqueue(new MockResponse().setResponseCode(400));

            var request = new CoffeeOrderRequest("ESPRESSO");

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
    }

    @Nested
    @DisplayName("unavailable")
    class UnavailableTests {

        @Test
        @DisplayName("Should throw CoffeeOrderProcessingException when machine is unavailable")
        void should_ThrowCoffeeOrderProcessingException_when_MachineIsUnavailable() {
            server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

            var request = new CoffeeOrderRequest("ESPRESSO");

            assertThatThrownBy(() -> service.order(request))
                    .isInstanceOf(CoffeeOrderProcessingException.class);
        }

        @Test
        @DisplayName("Should persist failed order when machine is unavailable")
        void should_PersistFailedOrder_when_MachineIsUnavailable() {
            server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

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

    @TestConfiguration
    static class TestCoffeeJobConfiguration {

        @Bean
        @Primary
        CoffeeJobTracker testCoffeeJobTracker() {
            return Mockito.mock(CoffeeJobTracker.class);
        }
    }
}
