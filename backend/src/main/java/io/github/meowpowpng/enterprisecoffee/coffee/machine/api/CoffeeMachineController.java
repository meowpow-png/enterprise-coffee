package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
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

    /**
     * Returns the current coffee machine status.
     *
     * @return current coffee machine status
     * @throws CoffeeMachineUnavailableException if communication with the machine fails
     * @throws CoffeeMachineProtocolException if the machine returns an invalid response
     */
    @GetMapping(ApiEndpoints.MACHINE_STATUS)
    MachineStatusResponse status() {
        return new MachineStatusResponse(client.status().name());
    }

    /**
     * Returns the current coffee brewing progress.
     *
     * @return current coffee-brewing progress
     * @throws CoffeeMachineUnavailableException if communication with the machine fails
     * @throws CoffeeMachineProtocolException if the machine returns an invalid response
     */
    @GetMapping(ApiEndpoints.MACHINE_PROGRESS)
    MachineProgressResponse progress() {
        var progress = client.progress();

        return new MachineProgressResponse(
                progress.type() != null ? progress.type().value() : "",
                progress.progress().value()
        );
    }
}
