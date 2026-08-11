package com.plazoleta.notification_service.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PinValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pin {

    String message() default "El campo type solo puede ser 1 (SMS) o 2 (WhatsApp)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
