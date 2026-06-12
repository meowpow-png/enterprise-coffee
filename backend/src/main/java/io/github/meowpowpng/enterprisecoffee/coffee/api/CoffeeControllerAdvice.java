package io.github.meowpowpng.enterprisecoffee.coffee.api;

import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderProcessingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles exceptions raised by coffee controllers.
 */
@RestControllerAdvice(assignableTypes = CoffeeController.class)
public class CoffeeControllerAdvice {

    /**
     * Handles invalid coffee order requests.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CoffeeOrderInvalidException.class)
    ClientOrderResponse handleInvalidOrder(CoffeeOrderInvalidException exception) {
        return new ClientOrderResponse(exception.getMessage());
    }

    /**
     * Handles coffee order processing failures.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CoffeeOrderProcessingException.class)
    ClientOrderResponse handleProcessingFailure(CoffeeOrderProcessingException exception) {
        return new ClientOrderResponse(exception.getMessage());
    }
}
