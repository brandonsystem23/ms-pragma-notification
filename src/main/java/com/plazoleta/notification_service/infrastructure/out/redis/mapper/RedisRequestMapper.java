package com.plazoleta.notification_service.infrastructure.out.redis.mapper;

import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.infrastructure.out.redis.dto.NotificationRedisValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedisRequestMapper {

    NotificationRedisValue toInsert(NotificationData notificationData);
}
