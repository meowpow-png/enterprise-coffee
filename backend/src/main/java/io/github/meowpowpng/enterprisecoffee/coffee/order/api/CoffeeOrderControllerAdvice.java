package io.github.meowpowpng.enterprisecoffee.coffee.order.api;

import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.order.api.exception.CoffeeOrderProcessingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles exceptions raised by coffee controllers.
 */
@RestControllerAdvice(assignableTypes = CoffeeOrderController.class)
public class CoffeeOrderControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CoffeeOrderInvalidException.class)
    CoffeeOrderResponse handleInvalidOrder(CoffeeOrderInvalidException exception) {
        return new CoffeeOrderResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CoffeeOrderProcessingException.class)
    CoffeeOrderResponse handleProcessingFailure(CoffeeOrderProcessingException exception) {
        return new CoffeeOrderResponse(exception.getMessage());
    }
}
