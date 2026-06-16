package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

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

    @PostMapping(ApiEndpoints.COFFEE_ORDER)
    @ResponseStatus(HttpStatus.ACCEPTED)
    CoffeeOrderResponse order(@Valid @RequestBody CoffeeOrderRequest request) {
        return service.order(request);
    }
}
