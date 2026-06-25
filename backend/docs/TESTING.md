# Testing

## Test Suites

The backend uses two complementary test suites:

- Unit tests provide fast feedback by verifying individual classes in isolation
- Integration tests verify the interaction between multiple components and their infrastructure

### Unit Tests

Unit tests verify individual classes in isolation without starting a Spring application context. External dependencies are replaced with test doubles, allowing business logic to be validated quickly and deterministically.

Run the unit test suite:

```shell
./gradlew test
````

### Integration Tests

Integration tests verify the interaction between multiple application components. Depending on the scenario, they may validate Spring configuration, persistence, HTTP endpoints, communication with external services, or complete application workflows.

Run the integration test suite:

```shell
./gradlew integrationTest
```

## Annotations

The project provides several composed test annotations that encapsulate common Spring testing configuration. Contributors should prefer these annotations over applying individual Spring test annotations directly.

### @IntegrationTest

Activates the `test` Spring profile for integration tests. This ensures that `application-test.properties` is always loaded in addition to the production configuration, regardless of how the tests are executed.

This annotation should be applied to any test that starts a Spring application context and relies on application configuration, such as tests annotated with `@SpringBootTest`.

### @JpaIntegrationTest

Configures a JPA test slice for integration tests that only require the persistence layer. The annotation activates the `test` profile, configures Spring Data JPA testing, and preserves the Testcontainers-backed datasource instead of replacing it with an embedded database.

This annotation should be used for repository and persistence tests that do not require the full Spring Boot application context.

### @MockMvcIntegrationTest

Configures the MockMvc test infrastructure for controller integration tests. The annotation enables Spring's `MockMvc` support and registers the project's `MockMvcSupport` fixture, providing convenient access to the configured `MockMvc` and `ObjectMapper` instances.

This annotation should be used for integration tests that verify HTTP endpoints through the Spring MVC layer.

### @DisableAsync

Disables asynchronous execution by replacing the application's asynchronous executor with a synchronous implementation. This causes tasks normally executed on background threads to run on the calling thread, making test execution deterministic.

This annotation should be used by integration tests that verify asynchronous workflows or depend on asynchronous processing completing before assertions are performed.

## Infrastructure

### Testcontainers

Integration tests use Testcontainers to provision an isolated PostgreSQL database automatically. The project uses the Testcontainers JDBC driver, allowing Spring Boot to start a container transparently based on the configured datasource URL.

As a result, integration tests require no dedicated Testcontainers configuration or annotations and execute against a clean database instance by default.

The database container is shared across the integration test suite and is not recreated between individual tests. Data should therefore be cleaned up explicitly, either by relying on transactional rollback, executing cleanup SQL, or removing persisted entities through JPA repositories.

### MockMvc

HTTP endpoints are tested using `MockMvcSupport`, a shared test fixture that provides access to both `MockMvc` and the configured `ObjectMapper`. This avoids repeated test setup and ensures all controller integration tests use the same testing infrastructure.

```java
@Autowired
private MockMvcSupport support;

private MockMvc mockMvc;

@BeforeEach
void setup() {
    this.mockMvc = support.mockMvc();
}
```

The underlying `MockMvc` instance executes requests against the Spring MVC layer without starting an external web server, allowing controller behavior to be tested efficiently while exercising request mapping, validation, serialization, and exception handling.

### MockWebServer

Outbound HTTP communication is tested using `MockWebServerTest`, a shared base class that manages the lifecycle of an embedded `MockWebServer`. Tests inherit from this class and configure the application to communicate with the mock server instead of the real coffee machine.

```java
class DefaultCoffeeOrderServiceTest extends MockWebServerTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("coffee.machine.base-url",() -> server.url("/").toString());
    }
}
```

Mock responses can then be enqueued to simulate different machine behaviors:

```java
server.enqueue(new MockResponse().setResponseCode(202));
```

## Frameworks

### Wiring Test Framework

Spring configuration is verified using a lightweight testing framework built on top of `ApplicationContextRunner`. The framework provides a fluent API for configuring the application context and asserting bean registration, transaction management, scheduling, and other wiring concerns without starting the full application.

The following example verifies that `MachineConfiguration` registers the expected `MachineProperties` bean and the application context starts successfully when the required configuration properties are provided:

```java
@Test
void should_RegisterMachineProperties_when_MachineConfigurationIsLoaded() {
    TestApplicationContextRunner.from(new ApplicationContextRunner())
            .withConfiguration(MachineConfiguration.class)
            .withPropertyValues(machineProperties())
            .hasBean(MachineProperties.class)
            .doesNotFail();
}
```

The framework also supports custom bean assertions, allowing tests to verify configured infrastructure components directly.

```java
TestApplicationContextRunner.from(new ApplicationContextRunner())
        .withConfiguration(MachineConfiguration.class)
        .withPropertyValues(machineProperties(server.url("/").toString()))
        .withBean(RestClient.class, RestClientTests::assertConfiguredRestClient)
        .doesNotFail();
```

## Logging

Tests use a dedicated `logback-test.xml` configuration to provide consistent console output during local development and CI execution. The default log level is `INFO` and can be overridden using the `TEST_LOG_LEVEL` environment variable.

For example, to enable debug logging when running tests locally:

```shell
TEST_LOG_LEVEL=DEBUG ./gradlew test
```

In CI, the same variable can be configured through repository secrets or workflow environment variables, allowing the logging verbosity to be adjusted without modifying the test configuration.

## Code Coverage

Code coverage is generated using JaCoCo. The `jacocoTestReport` task aggregates execution data from both unit and integration test suites, producing a single coverage report for the entire application.

Generate the coverage report:

```shell
./gradlew jacocoTestReport
```

The task produces both HTML and XML reports. The HTML report is intended for local inspection, while the XML report is used by CI for coverage reporting.
