package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.internal.config.MachineConfiguration;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.internal.config.MachineProperties;
import io.github.meowpowpng.enterprisecoffee.support.MockWebServerTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;

import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

import okhttp3.mockwebserver.MockResponse;
import org.awaitility.Durations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class MachineConfigurationTest {

    @Nested
    @DisplayName("bean")
    class BeanTests {

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
    }

    @Nested
    @DisplayName("restClient")
    class RestClientTests extends MockWebServerTest {

        @Test
        @DisplayName("Should configure rest client from machine properties when machine configuration is loaded")
        void should_ConfigureRestClientFromMachineProperties_when_MachineConfigurationIsLoaded() {
            server.enqueue(new MockResponse().setResponseCode(200));
            Consumer<RestClient> assertion = client -> {
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
            };
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(machineProperties(server.url("/").toString()))
                    .withBean(RestClient.class, assertion)
                    .doesNotFail();
        }
    }

    @Nested
    @DisplayName("property")
    class PropertyTests {

        @Test
        @DisplayName("Should bind base URL when valid machine properties are provided")
        void should_BindBaseUrl_when_ValidMachinePropertiesAreProvided() {
            var baseUrl = "https://example.com";

            Consumer<MachineProperties> assertion = properties ->
                    assertThat(properties.baseUrl()).isEqualTo(baseUrl);

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(machineProperties(baseUrl))
                    .hasBean(MachineProperties.class)
                    .withBean(MachineProperties.class, assertion)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should bind connect timeout when valid machine properties are provided")
        void should_BindConnectTimeout_when_ValidMachinePropertiesAreProvided() {
            var connectTimeout = Duration.ofSeconds(5);
            var properties = machineProperties(
                    "http://localhost",
                    connectTimeout,
                    Duration.ofSeconds(1)
            );
            Consumer<MachineProperties> assertion = p ->
                    assertThat(p.connectTimeout()).isEqualTo(connectTimeout);

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(properties)
                    .hasBean(MachineProperties.class)
                    .withBean(MachineProperties.class, assertion)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should bind read timeout when valid machine properties are provided")
        void should_BindReadTimeout_when_ValidMachinePropertiesAreProvided() {
            var readTimeout = Duration.ofSeconds(5);
            var properties = machineProperties(
                    "http://localhost",
                    Duration.ofSeconds(1),
                    readTimeout
            );
            Consumer<MachineProperties> assertion = p ->
                    assertThat(p.readTimeout()).isEqualTo(readTimeout);

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(properties)
                    .hasBean(MachineProperties.class)
                    .withBean(MachineProperties.class, assertion)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should fail when base URL is blank")
        void should_Fail_when_BaseUrlIsBlank() {
            var properties = machineProperties(
                    " ",
                    Duration.ofSeconds(1),
                    Duration.ofSeconds(1)
            );
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(properties)
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when connect timeout is missing")
        void should_Fail_when_ConnectTimeoutIsMissing() {
            var properties = new String[]{
                    "coffee.machine.base-url=http://localhost",
                    "coffee.machine.read-timeout=PT1S"
            };
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(properties)
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when connect timeout is zero")
        void should_Fail_when_ConnectTimeoutIsZero() {
            var properties = machineProperties(
                    "http://localhost",
                    Duration.ZERO,
                    Duration.ofSeconds(1)
            );
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(properties)
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when read timeout is zero")
        void should_Fail_when_ReadTimeoutIsZero() {
            var properties = machineProperties(
                    "http://localhost",
                    Duration.ofSeconds(1),
                    Duration.ZERO
            );
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(MachineConfiguration.class)
                    .withPropertyValues(properties)
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }
    }

    private static String[] machineProperties(
            String baseUrl,
            Duration connectTimeout,
            Duration readTimeout
    ) {
        return new String[]{
                "coffee.machine.base-url=" + baseUrl,
                "coffee.machine.connect-timeout=" + connectTimeout,
                "coffee.machine.read-timeout=" + readTimeout,
        };
    }

    private static String[] machineProperties(String baseUrl) {
        return machineProperties(baseUrl, Durations.ONE_SECOND, Durations.ONE_SECOND);
    }

    private static String[] machineProperties() {
        return machineProperties("http://localhost");
    }
}
