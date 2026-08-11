package com.plazoleta.notification_service.domain.model;

import lombok.Builder;

@Builder
public record PinCode(
        String phone,
        String pin
) {
}
