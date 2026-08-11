package com.plazoleta.notification_service.domain.port.in;

import com.plazoleta.notification_service.domain.model.Notification;
import reactor.core.publisher.Mono;

public interface SendNotificationUseCase {

    Mono<Notification> send(
            String token,
            String phone
    );
}
