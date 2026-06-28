package io.github.meowpowpng.enterprisecoffee.infra.config;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "Allowed origin must not be blank")
@URL(message = "Allowed origin must be a valid URL")
@Constraint(validatedBy = {})
@interface AllowedOrigin {

    String message() default "Invalid allowed origin";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
