package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.internal.config.MachineConfiguration;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.internal.config.MachineProperties;
import io.github.meowpowpng.enterprisecoffee.support.MockWebServerTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

import okhttp3.mockwebserver.MockResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MachineConfigurationTest {

    @Test
    @DisplayName("Should register machine properties when machine configuration is loaded")
    void should_RegisterMachineProperties_when_MachineConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(MachineConfiguration.class)
                .withPropertyValues(machineProperties())
                .hasBean(MachineProperties.class)
                .doesNotFail();
    }

    @Test
    @DisplayName("Should register rest client when machine configuration is loaded")
    void should_RegisterRestClient_when_MachineConfigurationIsLoaded() {
        TestApplicationContextRunner.from(new ApplicationContextRunner())
                .withConfiguration(MachineConfiguration.class)
                .withPropertyValues(machineProperties())
                .hasBean(RestClient.class)
                .doesNotFail();
    }

    @Nested
    @DisplayName("restClient")
    class RestClientTests extends MockWebServerTest {

        @Test
        @DisplayName("Should configure rest client from machine properties when machine configuration is loaded")
        void should_ConfigureRestClientFromMachineProperties_when_MachineConfigurationIsLoaded() {
            server.enqueue(new MockResponse().setResponseCode(200));

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(machineProperties(server.url("/").toString()))
                    .withBean(RestClient.class, RestClientTests::assertConfiguredRestClient)
                    .doesNotFail();
        }

        private static void assertConfiguredRestClient(RestClient client) {
            client.get()
                    .uri("/health")
                    .retrieve()
                    .toBodilessEntity();

            try {
                var request = server.takeRequest();

                assertThat(request.getMethod()).isEqualTo("GET");
                assertThat(request.getPath()).isEqualTo("/health");
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String[] machineProperties(String baseUrl) {
        return new String[]{
                "coffee.machine.base-url=" + baseUrl,
                "coffee.machine.connect-timeout=PT1S",
                "coffee.machine.read-timeout=PT1S"
        };
    }

    private static String[] machineProperties() {
        return machineProperties("http://localhost");
    }
}
