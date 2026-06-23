package io.github.meowpowpng.enterprisecoffee.infra.support;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class GlobalTestController {

    @PostMapping("/validate")
    void validate(@Valid @RequestBody ValidationRequest ignored) {}

    @PostMapping("/boom")
    void boom() {
        throw new RuntimeException("boom");
    }
}
