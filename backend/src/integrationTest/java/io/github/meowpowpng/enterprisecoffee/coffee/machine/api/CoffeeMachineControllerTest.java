package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.common.ApiEndpoints;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcIntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import org.jspecify.annotations.NullMarked;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@MockMvcIntegrationTest
@Import(CoffeeMachineControllerConfiguration.class)
class CoffeeMachineControllerTest {

    @Autowired
    private MockMvcSupport support;

    @Autowired
    private CoffeeMachineClient client;

    private TestCoffeeMachineClient testClient;
    private MockMvc mockMvc;

    @BeforeEach
    void setupCoffeeMachineControllerTest() {
        this.testClient = (TestCoffeeMachineClient) client;
        this.mockMvc = support.mockMvc();
    }

    @AfterEach
    void teardownCoffeeMachineControllerTest() {
        testClient.reset();
    }

    @Test
    @DisplayName("Should return status when status is requested")
    void should_ReturnStatus_when_StatusIsRequested() throws Exception {
        testClient.status(CoffeeMachineStatus.BREWING);

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

        testClient.progress(new MachineCoffeeProgress(
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

        testClient.progress(new MachineCoffeeProgress(
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
        testClient.markUnavailable();

        mockMvc.perform(get(ApiEndpoints.MACHINE_STATUS))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message")
                        .value(TestCoffeeMachineClient.UNAVAILABLE_MESSAGE));
    }

    @Test
    @DisplayName("Should return internal server error when machine protocol fails")
    void should_ReturnInternalServerError_when_MachineProtocolFails() throws Exception {
        testClient.markProtocolFailure();

        mockMvc.perform(get(ApiEndpoints.MACHINE_STATUS))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value(TestCoffeeMachineClient.PROTOCOL_FAILURE_MESSAGE));
    }

    @NullMarked
    static final class TestCoffeeMachineClient implements CoffeeMachineClient {

        public static final String UNAVAILABLE_MESSAGE = "machine unavailable";
        public static final String PROTOCOL_FAILURE_MESSAGE = "protocol failure";

        private CoffeeMachineStatus status;
        private MachineCoffeeProgress progress;
        private boolean isAvailable, protocolFails;

        TestCoffeeMachineClient() {
            this.status = CoffeeMachineStatus.READY;
            this.progress = new MachineCoffeeProgress(
                    new CoffeeType("ESPRESSO"),
                    Progress.of(50)
            );
            this.isAvailable = true;
        }

        @Override
        public CoffeeMachineStatus status() {
            if (!isAvailable) {
                throw new CoffeeMachineUnavailableException(
                        UNAVAILABLE_MESSAGE,
                        new RuntimeException("boom")
                );
            }
            if (protocolFails) {
                throw new CoffeeMachineProtocolException(
                        PROTOCOL_FAILURE_MESSAGE,
                        new RuntimeException("boom")
                );
            }
            return status;
        }

        @Override
        public MachineCoffeeProgress progress() {
            return progress;
        }

        @Override
        public MachineOrderResult order(CoffeeType type) {
            return MachineOrderResult.ACCEPTED;
        }

        void status(CoffeeMachineStatus status) {
            this.status = status;
        }

        void progress(MachineCoffeeProgress progress) {
            this.progress = progress;
        }

        void markUnavailable() {
            isAvailable = false;
        }

        void markProtocolFailure() {
            protocolFails = true;
        }

        void reset() {
            this.status = CoffeeMachineStatus.READY;
            this.progress = new MachineCoffeeProgress(
                    new CoffeeType("ESPRESSO"),
                    Progress.of(50)
            );
            isAvailable = true;
            protocolFails = false;
        }
    }
}
