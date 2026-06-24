package io.github.meowpowpng.enterprisecoffee.infra.support;

import jakarta.validation.constraints.NotBlank;

public record ValidationRequest(
        @NotBlank(message = "value must not be blank")
        String value
) {
    public static ValidationRequest empty() {
        return new ValidationRequest("");
    }
}
