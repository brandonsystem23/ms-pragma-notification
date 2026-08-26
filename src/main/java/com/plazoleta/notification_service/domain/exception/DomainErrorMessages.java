package com.plazoleta.notification_service.domain.exception;

public final class DomainErrorMessages {

    private DomainErrorMessages() {
    }

    public static final String PIN_STORAGE_ERROR = "No se pudo almacenar el PIN";
    public static final String PHONE_REQUIRED = "El campo phone es obligatorio";
    public static final String PHONE_MAX_LENGTH = "El phone no puede tener más de 13 caracteres";
    public static final String PHONE_INVALID = "El phone solo puede contener números y opcionalmente iniciar con +";
    public static final String PIN_LENGTH_INVALID = "La longitud del PIN debe ser entre 4 y 6";
    public static final String NOTIFICATION_SEND_ERROR = "No fue posible enviar la notificación";
}
