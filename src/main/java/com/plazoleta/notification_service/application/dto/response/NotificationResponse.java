package com.plazoleta.notification_service.application.dto.response;

import lombok.Builder;

@Builder
public record NotificationResponse(
        String message,
        String channel,
        String destination,
        String pin
) {
}
