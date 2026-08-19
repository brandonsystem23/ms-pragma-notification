package com.plazoleta.notification_service.domain.model;

import lombok.Builder;

@Builder
public record Notification(

        String phone,

        String message
) {
}
