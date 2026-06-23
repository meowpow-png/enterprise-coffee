package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.common.ApiEndpoints;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcIntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@MockMvcIntegrationTest
@Import(CoffeeMachineControllerTest.Configuration.class)
class CoffeeMachineControllerTest {

    @Autowired
    private MockMvcSupport support;

    @Autowired
    private StubCoffeeMachineClient client;

    private MockMvc mockMvc;

    @BeforeEach
    void setupCoffeeMachineControllerTest() {
        this.mockMvc = support.mockMvc();
    }

    @AfterEach
    void teardownCoffeeMachineControllerTest() {
        client.reset();
    }

    @Test
    @DisplayName("Should return status when status is requested")
    void should_ReturnStatus_when_StatusIsRequested() throws Exception {
        client.status(CoffeeMachineStatus.BREWING);

        mockMvc.perform(get(ApiEndpoints.MACHINE_STATUS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(CoffeeMachineStatus.BREWING.name()));
    }

    @Test
    @DisplayName("Should return progress when progress is requested")
    void should_ReturnProgress_when_ProgressIsRequested() throws Exception {
        var expectedType = "LATTE";
        var expectedProgress = 75;

        client.progress(new MachineCoffeeProgress(
                new CoffeeType(expectedType),
                Progress.of(expectedProgress)
        ));
        mockMvc.perform(get(ApiEndpoints.MACHINE_PROGRESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(expectedType))
                .andExpect(jsonPath("$.progress").value(expectedProgress));
    }

    @Test
    @DisplayName("Should return empty type when progress has no coffee type")
    void should_ReturnEmptyType_when_ProgressHasNoCoffeeType() throws Exception {
        var expectedProgress = 75;

        client.progress(new MachineCoffeeProgress(
                null,
                Progress.of(expectedProgress)
        ));
        mockMvc.perform(get(ApiEndpoints.MACHINE_PROGRESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type")
                        .value(""))
                .andExpect(jsonPath("$.progress")
                        .value(expectedProgress));
    }

    @Test
    @DisplayName("Should return service unavailable when machine is unavailable")
    void should_ReturnServiceUnavailable_when_MachineIsUnavailable() throws Exception {
        client.markUnavailable();

        mockMvc.perform(get(ApiEndpoints.MACHINE_STATUS))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message")
                        .value(StubCoffeeMachineClient.UNAVAILABLE_MESSAGE));
    }

    @Test
    @DisplayName("Should return internal server error when machine protocol fails")
    void should_ReturnInternalServerError_when_MachineProtocolFails() throws Exception {
        client.markProtocolFailure();

        mockMvc.perform(get(ApiEndpoints.MACHINE_STATUS))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value(StubCoffeeMachineClient.PROTOCOL_FAILURE_MESSAGE));
    }

    @TestConfiguration
    static class Configuration {

        @Bean
        StubCoffeeMachineClient stubCoffeeMachineClient() {
            return new StubCoffeeMachineClient();
        }

        @Bean
        @Primary
        CoffeeMachineClient testCoffeeMachineClient(StubCoffeeMachineClient client) {
            return client;
        }
    }
}
