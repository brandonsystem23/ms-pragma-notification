package com.plazoleta.notification_service.infrastructure.output.notification;

import com.plazoleta.notification_service.domain.model.NotificationChannel;
import com.plazoleta.notification_service.domain.model.NotificationMessage;
import com.plazoleta.notification_service.domain.port.out.NotificationSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MockWhatsappNotificationSenderAdapter implements NotificationSenderPort {

    @Override
    public NotificationChannel supportedChannel() {
        return NotificationChannel.WHATSAPP;
    }

    @Override
    public Mono<Void> send(NotificationMessage notificationMessage) {
        log.info("=== MOCK WHATSAPP NOTIFICATION ===");
        log.info("Canal: {}", supportedChannel());
        log.info("Destino: {}", notificationMessage.phone());
        log.info("Mensaje: {}", notificationMessage.message());
        log.info("==================================");
        return Mono.empty();
    }
}
