package io.github.meowpowpng.enterprisecoffee.infra;

import io.github.meowpowpng.enterprisecoffee.infra.support.ValidationRequest;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcIntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.MockMvcSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@MockMvcIntegrationTest
class GlobalControllerAdviceTest {

    @Autowired
    private MockMvcSupport support;

    @Test
    @DisplayName("Should return bad request when request validation fails")
    void should_ReturnBadRequest_when_RequestValidationFails() throws Exception {
        var request = post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(support.mapper().writeValueAsString(ValidationRequest.empty()));

        support.mockMvc().perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("value must not be blank"));
    }

    @Test
    @DisplayName("Should return internal server error when unexpected exception is thrown")
    void should_ReturnInternalServerError_when_UnexpectedExceptionIsThrown() throws Exception {
        support.mockMvc().perform(post("/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("internal server error"));
    }
}
