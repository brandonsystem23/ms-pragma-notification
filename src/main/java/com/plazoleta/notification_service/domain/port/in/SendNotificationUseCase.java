package com.plazoleta.notification_service.domain.port.in;

import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import reactor.core.publisher.Mono;

public interface SendNotificationUseCase {

    Mono<NotificationResponse> send(
            String token,
            Integer type,
            String phone
    );
}
