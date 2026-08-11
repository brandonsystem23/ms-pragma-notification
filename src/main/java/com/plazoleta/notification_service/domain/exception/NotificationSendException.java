package com.plazoleta.notification_service.domain.exception;

public class NotificationSendException extends RuntimeException {

    public NotificationSendException(String message) {
        super(message);
    }
}
