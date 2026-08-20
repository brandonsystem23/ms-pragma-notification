package com.plazoleta.notification_service.domain.model;

import lombok.Builder;

@Builder
public record NotificationData(

        String phoneNumber,

        String pin
) {
}
