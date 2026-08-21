package com.plazoleta.notification_service.domain.spi;

import com.plazoleta.notification_service.domain.model.NotificationData;
import reactor.core.publisher.Mono;

public interface INotificationSenderPort {

    Mono<Void> send(NotificationData notificationData);
}
