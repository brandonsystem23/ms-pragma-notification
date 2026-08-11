package com.plazoleta.notification_service.domain.model.auth;

import lombok.Builder;

@Builder
public record AuthSession(

        Long userId,
        String role,
        String numberDocument,
        String phone,
        String email
) {
}
