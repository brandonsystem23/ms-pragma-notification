package com.plazoleta.notification_service.domain.port.out;

import com.plazoleta.notification_service.domain.model.NotificationChannel;
import com.plazoleta.notification_service.domain.model.NotificationMessage;
import reactor.core.publisher.Mono;

public interface NotificationSenderPort {

    NotificationChannel supportedChannel();

    Mono<Void> send(NotificationMessage notificationMessage);
}
