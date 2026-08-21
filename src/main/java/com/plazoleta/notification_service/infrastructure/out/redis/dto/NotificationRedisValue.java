package com.plazoleta.notification_service.infrastructure.out.redis.dto;

import lombok.Builder;

@Builder
public record NotificationRedisValue(

        String phoneNumber,

        String pin
) {
}
