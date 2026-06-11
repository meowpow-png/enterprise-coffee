package io.github.meowpowpng.enterprisecoffee.coffee.api;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates coffee type values.
 */
public final class CoffeeTypeValidator implements ConstraintValidator<ValidCoffeeType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            CoffeeType.valueOf(value);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
}
