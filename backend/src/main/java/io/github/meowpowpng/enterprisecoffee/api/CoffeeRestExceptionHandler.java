package io.github.meowpowpng.enterprisecoffee.api;

import io.github.meowpowpng.enterprisecoffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.api.exception.CoffeeOrderProcessingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles REST API exceptions for coffee domain.
 */
@RestControllerAdvice
public class CoffeeRestExceptionHandler {

    /**
     * Handles invalid coffee order requests.
     */
    @ExceptionHandler(CoffeeOrderInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ClientOrderResponse handleInvalidOrder(CoffeeOrderInvalidException exception) {
        return new ClientOrderResponse(exception.getMessage());
    }

    /**
     * Handles coffee order processing failures.
     */
    @ExceptionHandler(CoffeeOrderProcessingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ClientOrderResponse handleProcessingFailure(CoffeeOrderProcessingException exception) {
        return new ClientOrderResponse(exception.getMessage());
    }
}
