package com.plazoleta.notification_service.infrastructure.out.jwt.dto;

import lombok.Builder;

@Builder
public record AuthenticatedUser(
        Long userId,
        String fullName,
        String role,
        String numberDocument,
        String phone,
        String email
) {
}
