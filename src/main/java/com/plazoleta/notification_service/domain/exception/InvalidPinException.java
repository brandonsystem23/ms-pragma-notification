package com.plazoleta.notification_service.domain.exception;

public class InvalidPinException extends RuntimeException {

    public InvalidPinException(int min, int max) {

        super("La longitud del PIN dede ser entre " + min + " y " + max);
    }
}
