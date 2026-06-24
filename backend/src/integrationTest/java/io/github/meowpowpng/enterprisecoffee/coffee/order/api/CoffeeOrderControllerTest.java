package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import io.github.meowpowpng.enterprisecoffee.common.ApiEndpoints;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcIntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@MockMvcIntegrationTest
@Import(CoffeeOrderControllerConfiguration.class)
class CoffeeOrderControllerTest {

    @Autowired
    private MockMvcSupport support;

    @Autowired
    private TestCoffeeOrderService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setupCoffeeOrderControllerTest() {
        this.mockMvc = support.mockMvc();
    }

    @AfterEach
    void teardownCoffeeOrderControllerTest() {
        service.reset();
    }

    @Test
    @DisplayName("Should return accepted when coffee order is placed")
    void should_ReturnAccepted_when_CoffeeOrderIsPlaced() throws Exception {
        var mapper = support.mapper();
        var request = post(ApiEndpoints.COFFEE_ORDER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(new CoffeeOrderRequest("ESPRESSO")));

        var response = new CoffeeOrderResponse("accepted");

        service.response(response);

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
                .content(mapper.writeValueAsBytes(new CoffeeOrderRequest("ESPRESSO")));

        service.markInvalidOrder();

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
                .content(mapper.writeValueAsBytes(new CoffeeOrderRequest("ESPRESSO")));

        service.markProcessingFailure();

        mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(TestCoffeeOrderService.PROCESSING_FAILURE_MESSAGE));
    }
}
