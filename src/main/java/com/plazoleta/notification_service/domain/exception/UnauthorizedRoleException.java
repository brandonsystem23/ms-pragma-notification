package com.plazoleta.notification_service.domain.exception;

public class UnauthorizedRoleException extends RuntimeException {

    public UnauthorizedRoleException() {
        super("Solo un EMPLEADO puede enviar el PIN");
    }
}
