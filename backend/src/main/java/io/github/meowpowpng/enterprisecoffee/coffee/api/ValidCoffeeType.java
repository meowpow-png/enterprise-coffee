package io.github.meowpowpng.enterprisecoffee.coffee.api;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a value
 * must be a supported coffee type.
 */
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CoffeeTypeValidator.class)
public @interface ValidCoffeeType {

    String message() default "unsupported coffee type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
