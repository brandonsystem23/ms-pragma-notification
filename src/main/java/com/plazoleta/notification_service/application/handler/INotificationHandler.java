package com.plazoleta.notification_service.application.handler;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import reactor.core.publisher.Mono;

public interface INotificationHandler {

    Mono<NotificationResponse> sendNotification(SendNotificationRequest request, String numberDocument);
}
