package com.plazoleta.notification_service.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PinValidator implements ConstraintValidator<Pin, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value == 1 || value == 2;
    }
}
