package com.plazoleta.notification_service.infrastructure.out.redis.mapper;

import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.infrastructure.out.redis.dto.AuthSessionRedisValue;
import com.plazoleta.notification_service.infrastructure.out.redis.dto.NotificationRedisValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisRequestMapper {

    AuthSession toDomain(AuthSessionRedisValue authSessionRedisValue);

    NotificationRedisValue toInsert(NotificationData notificationData);
}
