package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;
import io.github.meowpowpng.enterprisecoffee.common.ApiEndpoints;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Handles coffee order requests.
 */
@RestController
public class CoffeeOrderController {

    private final CoffeeOrderService service;

    CoffeeOrderController(CoffeeOrderService service) {
        this.service = service;
    }

    /**
     * Places a coffee order.
     *
     * @param request coffee order request
     *
     * @return coffee order result
     *
     * @throws CoffeeOrderInvalidException if the request cannot be
     * accepted because the specified coffee order is invalid
     * @throws CoffeeOrderProcessingException if the order cannot be processed
     * @throws CoffeeMachineProtocolException if the coffee machine returns an invalid response
     * @throws IllegalStateException if the machine client returned an unexpected result
     */
    @PostMapping(ApiEndpoints.COFFEE_ORDER)
    @ResponseStatus(HttpStatus.ACCEPTED)
    CoffeeOrderResponse order(@Valid @RequestBody CoffeeOrderRequest request) {
        return service.order(request);
    }
}
