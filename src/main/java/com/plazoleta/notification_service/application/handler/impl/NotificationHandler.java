package com.plazoleta.notification_service.application.handler.impl;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.handler.INotificationHandler;
import com.plazoleta.notification_service.application.mapper.NotificationDtoMapper;
import com.plazoleta.notification_service.domain.api.INotificationServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationHandler implements INotificationHandler {

    private final INotificationServicePort iNotificationServicePort;
    private final NotificationDtoMapper notificationDtoMapper;

    @Override
    public Mono<NotificationResponse> sendNotification(String token, SendNotificationRequest request) {
        return iNotificationServicePort.send(
                token,
                request.phoneNumber()
        ).map(notificationDtoMapper::toResponse);
    }
}
