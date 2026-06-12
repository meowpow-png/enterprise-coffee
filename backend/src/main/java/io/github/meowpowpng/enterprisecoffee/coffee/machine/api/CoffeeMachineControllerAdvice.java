package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CoffeeMachineController.class)
class CoffeeMachineControllerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(CoffeeMachineUnavailableException.class)
    MachineErrorResponse handleUnavailable(CoffeeMachineUnavailableException exception) {
        return new MachineErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CoffeeMachineProtocolException.class)
    MachineErrorResponse handleProtocolFailure(CoffeeMachineProtocolException exception) {
        return new MachineErrorResponse(exception.getMessage());
    }
}
