package com.plazoleta.notification_service.domain.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Token inválido o expirado");
    }
}
