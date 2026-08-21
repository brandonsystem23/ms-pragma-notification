package com.plazoleta.notification_service.domain.api;

import com.plazoleta.notification_service.domain.model.Notification;
import reactor.core.publisher.Mono;

public interface INotificationServicePort {

    Mono<Notification> send(
            String token,
            String phone
    );
}
