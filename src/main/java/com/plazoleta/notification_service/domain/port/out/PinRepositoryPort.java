package com.plazoleta.notification_service.domain.port.out;

import com.plazoleta.notification_service.domain.model.NotificationMessage;
import reactor.core.publisher.Mono;

public interface PinRepositoryPort {

    Mono<String> save(String phone, String pin, NotificationMessage notificationMessage);
}
