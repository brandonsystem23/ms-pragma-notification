package com.plazoleta.notification_service.domain.model;

import lombok.Getter;

@Getter
public enum NotificationChannel {
    SMS(1),
    WHATSAPP(2);

    private final int type;

    NotificationChannel(int type) {
        this.type = type;
    }

    public static NotificationChannel fromType(Integer type) {
        if (type == null) {
            throw new IllegalArgumentException("El type es obligatorio");
        }

        for (NotificationChannel channel : values()) {
            if (channel.type == type) {
                return channel;
            }
        }

        throw new IllegalArgumentException("El type debe ser 1 para SMS o 2 para WhatsApp");
    }
}
