package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.common.ApiEndpoints;

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

    @GetMapping(ApiEndpoints.MACHINE_STATUS)
    MachineStatusResponse status() {
        return new MachineStatusResponse(client.status().name());
    }

    @GetMapping(ApiEndpoints.MACHINE_PROGRESS)
    MachineProgressResponse progress() {
        var progress = client.progress();

        return new MachineProgressResponse(
                progress.type() != null ? progress.type().value() : "",
                progress.progress().value()
        );
    }
}
