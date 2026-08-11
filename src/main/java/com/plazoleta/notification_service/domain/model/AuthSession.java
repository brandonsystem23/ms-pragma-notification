package com.plazoleta.notification_service.domain.model;

import lombok.Builder;

@Builder
public record AuthSession(
        Long userId,
        String fullName,
        String role,
        String numberDocument,
        String phone,
        String email
) {
}
