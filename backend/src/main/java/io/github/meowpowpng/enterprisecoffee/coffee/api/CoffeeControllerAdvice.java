package io.github.meowpowpng.enterprisecoffee.coffee.api;

import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderInvalidException;
import io.github.meowpowpng.enterprisecoffee.coffee.api.exception.CoffeeOrderProcessingException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles exceptions raised by coffee controllers.
 */
@RestControllerAdvice
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

    /**
     * Handles invalid coffee order requests.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ClientOrderResponse handleInvalidRequest(MethodArgumentNotValidException e) {
        var message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("coffee order request is invalid");

        return new ClientOrderResponse(message);
    }
}
