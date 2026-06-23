package io.github.meowpowpng.enterprisecoffee.support;

import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public abstract class MockWebServerTest {

    protected static MockWebServer server;

    @BeforeAll
    static void setupMockWebServerTest() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void teardownMockWebServerTest() throws IOException {
        server.shutdown();
    }
}
