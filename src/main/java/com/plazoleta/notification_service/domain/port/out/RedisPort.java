package com.plazoleta.notification_service.domain.port.out;

import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import reactor.core.publisher.Mono;

public interface RedisPort {

    Mono<AuthSession> findByToken(String token);

    Mono<String> save(String numberDocument, String pin, NotificationData notificationData);
}
