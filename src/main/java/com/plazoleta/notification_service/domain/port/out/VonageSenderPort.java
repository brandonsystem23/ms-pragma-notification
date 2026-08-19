package com.plazoleta.notification_service.domain.port.out;

import com.plazoleta.notification_service.domain.model.NotificationData;
import reactor.core.publisher.Mono;

public interface VonageSenderPort {

    Mono<Void> send(NotificationData notificationData);
}
