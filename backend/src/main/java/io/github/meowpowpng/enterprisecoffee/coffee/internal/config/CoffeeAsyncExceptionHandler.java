package io.github.meowpowpng.enterprisecoffee.coffee.internal.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Handles uncaught exceptions
 * thrown by asynchronous methods.
 */
@NullMarked
public final class CoffeeAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CoffeeAsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(
            Throwable exception,
            Method method,
            @Nullable Object... parameters
    ) {
        log.error("Unhandled async exception in {}",
                method.getName(),
                exception
        );
    }
}
