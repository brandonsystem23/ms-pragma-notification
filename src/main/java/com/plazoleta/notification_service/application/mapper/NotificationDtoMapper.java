package com.plazoleta.notification_service.application.mapper;

import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.domain.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationDtoMapper {

    NotificationResponse toResponse(Notification notification);
}
