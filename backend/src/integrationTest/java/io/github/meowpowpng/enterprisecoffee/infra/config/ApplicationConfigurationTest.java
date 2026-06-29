package io.github.meowpowpng.enterprisecoffee.infra.config;

import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestApplicationContextRunner;

import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ApplicationConfigurationTest {

    @Nested
    @DisplayName("bean")
    class BeanTests {

        @Test
        @DisplayName("Should register application properties when application configuration is loaded")
        void should_RegisterApplicationProperties_when_ApplicationConfigurationIsLoaded() {
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(ApplicationConfiguration.class)
                    .withPropertyValues(validPropertyValues())
                    .hasBean(ApplicationProperties.class)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should register clock when application configuration is loaded")
        void should_RegisterClock_when_ApplicationConfigurationIsLoaded() {
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(ApplicationConfiguration.class)
                    .withPropertyValues(validPropertyValues())
                    .hasBean(Clock.class)
                    .doesNotFail();
        }
    }

    @Nested
    @DisplayName("property")
    class PropertyTests {

        @Test
        @DisplayName("Should bind single allowed origin when valid application properties are provided")
        void should_BindSingleAllowedOrigin_when_ValidApplicationPropertiesAreProvided() {
            var allowedOrigin = "https://example.com";

            Consumer<ApplicationProperties> assertion = properties ->
                    assertAllowedOrigins(properties, allowedOrigin);

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(ApplicationConfiguration.class)
                    .withPropertyValues(allowedOriginProperty(allowedOrigin))
                    .hasBean(ApplicationProperties.class)
                    .withBean(ApplicationProperties.class, assertion)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should bind multiple allowed origins when valid application properties are provided")
        void should_BindMultipleAllowedOrigins_when_ValidApplicationPropertiesAreProvided() {
            var firstOrigin = "https://example.com";
            var secondOrigin = "https://example.org";

            Consumer<ApplicationProperties> assertion = properties ->
                    assertAllowedOrigins(properties, firstOrigin, secondOrigin);

            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(ApplicationConfiguration.class)
                    .withPropertyValues(allowedOriginProperty(firstOrigin, secondOrigin))
                    .hasBean(ApplicationProperties.class)
                    .withBean(ApplicationProperties.class, assertion)
                    .doesNotFail();
        }

        @Test
        @DisplayName("Should fail when allowed origins are empty")
        void should_Fail_when_AllowedOriginsAreEmpty() {
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(ApplicationConfiguration.class)
                    .withPropertyValues(allowedOriginProperty())
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when allowed origin is blank")
        void should_Fail_when_AllowedOriginIsBlank() {
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(ApplicationConfiguration.class)
                    .withPropertyValues(allowedOriginProperty(" ", "https://example.com"))
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        @Test
        @DisplayName("Should fail when allowed origin is not a valid URL")
        void should_Fail_when_AllowedOriginIsNotAValidUrl() {
            TestApplicationContextRunner.from(new ApplicationContextRunner())
                    .withConfiguration(ApplicationConfiguration.class)
                    .withPropertyValues(allowedOriginProperty("not-a-url"))
                    .failsWithException(ConfigurationPropertiesBindException.class);
        }

        private void assertAllowedOrigins(ApplicationProperties properties, String... expectedOrigins) {
            assertThat(properties.allowedOrigins()).containsExactly(expectedOrigins);
        }
    }

    private static String allowedOriginProperty(String... origins) {
        return "app.allowed-origins=" + String.join(",", origins);
    }

    private static String[] validPropertyValues() {
        return new String[]{allowedOriginProperty("https://example.com")};
    }
}
