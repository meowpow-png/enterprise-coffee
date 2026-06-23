package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.CoffeeMachineStatus;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineCoffeeProgress;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.MachineOrderResult;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockWebServerTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@IntegrationTest
class HttpCoffeeMachineClientTest extends MockWebServerTest {

    @Autowired
    private HttpCoffeeMachineClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("coffee.machine.base-url", () -> server.url("/").toString());
    }

    @Nested
    @DisplayName("status")
    class StatusTests {

        @Test
        @DisplayName("Should return machine status when status is retrieved")
        void should_ReturnMachineStatus_when_StatusIsRetrieved() {
            var payload = new HttpCoffeeMachineClient.MachineStatusPayload("READY");

            server.enqueue(new MockResponse()
                    .addHeader("Content-Type", "application/json")
                    .setBody(objectMapper.writeValueAsString(payload))
            );
            assertThat(client.status()).isEqualTo(CoffeeMachineStatus.READY);
        }

        @Test
        @DisplayName("Should throw CoffeeMachineProtocolException when status response is invalid")
        void should_ThrowCoffeeMachineProtocolException_when_StatusResponseIsInvalid() {
            var payload = new HttpCoffeeMachineClient.MachineStatusPayload("INVALID");

            server.enqueue(new MockResponse()
                    .addHeader("Content-Type", "application/json")
                    .setBody(objectMapper.writeValueAsString(payload))
            );
            assertThatThrownBy(() -> client.status())
                    .isInstanceOf(CoffeeMachineProtocolException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeMachineUnavailableException when communication fails")
        void should_ThrowCoffeeMachineUnavailableException_when_CommunicationFails() {
            server.enqueue(new MockResponse()
                    .setSocketPolicy(SocketPolicy.NO_RESPONSE));

            assertThatThrownBy(() -> client.status())
                    .isInstanceOf(CoffeeMachineUnavailableException.class);
        }
    }

    @Nested
    @DisplayName("progress")
    class ProgressTests {

        @Test
        @DisplayName("Should return machine progress when progress is retrieved")
        void should_ReturnMachineProgress_when_ProgressIsRetrieved() {
            var payload = new HttpCoffeeMachineClient.MachineProgressPayload(
                    "ESPRESSO",
                    50
            );
            server.enqueue(new MockResponse()
                    .addHeader("Content-Type", "application/json")
                    .setBody(objectMapper.writeValueAsString(payload))
            );
            var expected = new MachineCoffeeProgress(
                    new CoffeeType("ESPRESSO"),
                    Progress.of(50)
            );
            assertThat(client.progress()).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should throw CoffeeMachineProtocolException when progress response is invalid")
        void should_ThrowCoffeeMachineProtocolException_when_ProgressResponseIsInvalid() {
            var payload = new HttpCoffeeMachineClient.MachineProgressPayload(
                    "ESPRESSO",
                    -1
            );
            server.enqueue(new MockResponse()
                    .addHeader("Content-Type", "application/json")
                    .setBody(objectMapper.writeValueAsString(payload))
            );
            assertThatThrownBy(() -> client.progress())
                    .isInstanceOf(CoffeeMachineProtocolException.class);
        }
    }

    @Nested
    @DisplayName("order")
    class OrderTests {

        @Test
        @DisplayName("Should return accepted when machine accepts order")
        void should_ReturnAccepted_when_MachineAcceptsOrder() {
            server.enqueue(new MockResponse().setResponseCode(202));

            assertThat(client.order(new CoffeeType("ESPRESSO")))
                    .isEqualTo(MachineOrderResult.ACCEPTED);
        }

        @Test
        @DisplayName("Should return busy when machine rejects order because it is busy")
        void should_ReturnBusy_when_MachineRejectsOrderBecauseItIsBusy() {
            server.enqueue(new MockResponse().setResponseCode(409));

            assertThat(client.order(new CoffeeType("ESPRESSO")))
                    .isEqualTo(MachineOrderResult.BUSY);
        }

        @Test
        @DisplayName("Should return invalid when machine rejects order as invalid")
        void should_ReturnInvalid_when_MachineRejectsOrderAsInvalid() {
            server.enqueue(new MockResponse().setResponseCode(400));

            assertThat(client.order(new CoffeeType("ESPRESSO")))
                    .isEqualTo(MachineOrderResult.INVALID);
        }

        @Test
        @DisplayName("Should throw CoffeeMachineProtocolException when order response is unexpected")
        void should_ThrowCoffeeMachineProtocolException_when_OrderResponseIsUnexpected() {
            server.enqueue(new MockResponse().setResponseCode(500));

            assertThatThrownBy(() -> client.order(new CoffeeType("ESPRESSO")))
                    .isInstanceOf(CoffeeMachineProtocolException.class);
        }
    }
}
