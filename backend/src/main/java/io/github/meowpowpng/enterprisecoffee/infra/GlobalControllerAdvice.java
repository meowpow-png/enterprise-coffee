package io.github.meowpowpng.enterprisecoffee.infra;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles exceptions raised by controllers.
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @SuppressWarnings("DataFlowIssue")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    MessageResponse handleInvalidRequest(MethodArgumentNotValidException e) {
        var result = e.getBindingResult();
        var fieldError = result.getFieldErrors()
                .stream()
                .findFirst()
                .orElse(null);

        if (fieldError != null) {
            var message = fieldError.getDefaultMessage();

            log.debug("event=REQUEST_VALIDATION_FAILED field={} message={}",
                    fieldError.getField(),
                    message
            );
            return new MessageResponse(message);
        }
        var message = result.getAllErrors()
                .stream()
                .findFirst()
                .orElseThrow()
                .getDefaultMessage();

        log.debug("event=REQUEST_VALIDATION_FAILED message={}", message);
        return new MessageResponse(message);
    }
}
