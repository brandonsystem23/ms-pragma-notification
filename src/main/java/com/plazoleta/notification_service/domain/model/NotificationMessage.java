package com.plazoleta.notification_service.domain.model;

import lombok.Builder;

@Builder
public record NotificationMessage(
        String phone,
        String message,
        NotificationChannel channel
) {
}
