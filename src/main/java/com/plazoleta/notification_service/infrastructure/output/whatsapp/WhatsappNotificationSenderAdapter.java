package com.plazoleta.notification_service.infrastructure.output.whatsapp;

import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.port.out.WhatsappNotificationSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WhatsappNotificationSenderAdapter implements WhatsappNotificationSenderPort {

    @Override
    public Mono<Void> send(NotificationData notificationData) {

        String message = "Tu pedido está listo. Tu pin de seguridad es: ".concat(notificationData.pin());

        log.info("=== MOCK WHATSAPP NOTIFICATION ===");
        log.info("Destino: {}", notificationData.phone());
        log.info("Mensaje: {}", message);
        log.info("==================================");
        return Mono.empty();
    }
}
