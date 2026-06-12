package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles coffee machine requests.
 */
@RestController
public class CoffeeMachineController {

    private final CoffeeMachineClient client;

    CoffeeMachineController(CoffeeMachineClient client) {
        this.client = client;
    }

    @GetMapping("/status")
    MachineStatusResponse status() {
        return new MachineStatusResponse(client.status().name());
    }

    @GetMapping("/progress")
    MachineProgressResponse progress() {
        var progress = client.progress();

        return new MachineProgressResponse(
                progress.type() != null ? progress.type().value() : "",
                progress.progress().value()
        );
    }
}
