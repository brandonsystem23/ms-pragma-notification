package com.plazoleta.notification_service.domain.spi;

import com.plazoleta.notification_service.domain.model.NotificationData;
import reactor.core.publisher.Mono;

public interface INotificationCachePort {

    Mono<String> save(String numberDocument, String pin, NotificationData notificationData);
}
