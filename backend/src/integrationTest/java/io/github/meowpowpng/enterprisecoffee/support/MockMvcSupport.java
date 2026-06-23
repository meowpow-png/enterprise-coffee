package io.github.meowpowpng.enterprisecoffee.support;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

@TestComponent
public class MockMvcSupport {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    public MockMvcSupport(MockMvc mockMvc, ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    public MockMvc mockMvc() {
        return mockMvc;
    }

    public ObjectMapper mapper() {
        return mapper;
    }
}
