package com.plazoleta.notification_service.domain.exception;

public class PinStorageException extends RuntimeException {

    public PinStorageException() {
        super("No se pudo almacenar el PIN");
    }
}