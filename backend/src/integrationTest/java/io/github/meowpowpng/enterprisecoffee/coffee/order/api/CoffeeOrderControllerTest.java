package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.TestCoffeeOrderRequest;
import io.github.meowpowpng.enterprisecoffee.common.ApiEndpoints;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcIntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcSupport;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.awaitility.Durations;

import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@MockMvcIntegrationTest
@Import(CoffeeOrderControllerConfiguration.class)
class CoffeeOrderControllerTest {

    @Autowired
    private MockMvcSupport support;

    @Autowired
    private CoffeeOrderService service;

    private TestCoffeeOrderService testService;
    private MockMvc mockMvc;

    @BeforeEach
    void setupCoffeeOrderControllerTest() {
        this.testService = (TestCoffeeOrderService) service;
        this.mockMvc = support.mockMvc();
    }

    @AfterEach
    void teardownCoffeeOrderControllerTest() {
        testService.reset();
    }

    @Nested
    @DisplayName("order")
    class OrderMethodTest {

        @Test
        @DisplayName("Should return accepted when coffee order is placed")
        void should_ReturnAccepted_when_CoffeeOrderIsPlaced() throws Exception {
            var mapper = support.mapper();
            var request = post(ApiEndpoints.COFFEE_ORDER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(TestCoffeeOrderRequest.create()));

            var response = new CoffeeOrderResponse("accepted");

            testService.response(response);

            mockMvc.perform(request)
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.message")
                            .value("accepted"));
        }

        @Test
        @DisplayName("Should return bad request when request validation fails")
        void should_ReturnBadRequest_when_RequestValidationFails() throws Exception {
            var mapper = support.mapper();
            var request = post(ApiEndpoints.COFFEE_ORDER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(new CoffeeOrderRequest("")));

            mockMvc.perform(request).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when coffee order is invalid")
        void should_ReturnBadRequest_when_CoffeeOrderIsInvalid() throws Exception {
            var mapper = support.mapper();
            var request = post(ApiEndpoints.COFFEE_ORDER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(TestCoffeeOrderRequest.create()));

            testService.markInvalidOrder();

            mockMvc.perform(request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(TestCoffeeOrderService.INVALID_ORDER_MESSAGE));
        }

        @Test
        @DisplayName("Should return conflict when coffee order processing fails")
        void should_ReturnConflict_when_CoffeeOrderProcessingFails() throws Exception {
            var mapper = support.mapper();
            var request = post(ApiEndpoints.COFFEE_ORDER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(TestCoffeeOrderRequest.create()));

            testService.markProcessingFailure();

            mockMvc.perform(request)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message")
                            .value(TestCoffeeOrderService.PROCESSING_FAILURE_MESSAGE));
        }
    }

    @Nested
    @DisplayName("orders")
    class OrdersMethodTests {

        @Test
        @DisplayName("Should return latest coffee orders when coffee orders exist")
        void should_ReturnLatestCoffeeOrders_when_CoffeeOrdersExist() throws Exception {
            var clock = TestClock.create(Instant.parse("2025-01-01T10:00:00Z"));
            var response = new CoffeeOrdersResponse(List.of(
                    new CoffeeOrderView(
                            UUID.randomUUID(),
                            "ESPRESSO",
                            "ACCEPTED",
                            clock.instant()
                    ),
                    new CoffeeOrderView(
                            UUID.randomUUID(),
                            "LATTE",
                            "REJECTED",
                            clock.advance(Durations.ONE_SECOND)
                    )
            ));
            testService.ordersResponse(response);

            mockMvc.perform(get(ApiEndpoints.COFFEE_ORDERS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orders[*].type", contains("ESPRESSO", "LATTE")));
        }

        @Test
        @DisplayName("Should use default limit when limit is omitted")
        void should_UseDefaultLimit_when_LimitIsOmitted() throws Exception {
            mockMvc.perform(get(ApiEndpoints.COFFEE_ORDERS))
                    .andExpect(status().isOk());

            assertThat(testService.limit()).isEqualTo(20);
        }

        @Test
        @DisplayName("Should use specified limit when limit is provided")
        void should_UseSpecifiedLimit_when_LimitIsProvided() throws Exception {
            var request = get(ApiEndpoints.COFFEE_ORDERS).param("limit", "5");
            mockMvc.perform(request).andExpect(status().isOk());

            assertThat(testService.limit()).isEqualTo(5);
        }
    }
}
