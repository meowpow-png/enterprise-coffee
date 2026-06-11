package io.github.meowpowpng.enterprisecoffee.coffee.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles coffee order requests.
 */
@RestController
public class CoffeeController {

    private final CoffeeOrderService service;

    CoffeeController(CoffeeOrderService service) {
        this.service = service;
    }

    @PostMapping("/order")
    ClientOrderResponse order(@RequestBody ClientOrderRequest request) {
        return service.order(request);
    }
}
