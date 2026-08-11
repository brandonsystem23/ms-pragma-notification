package com.plazoleta.notification_service.infrastructure.output.notification;

import com.plazoleta.notification_service.domain.model.NotificationChannel;
import com.plazoleta.notification_service.domain.port.out.NotificationSenderPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationSenderResolver {

    private final List<NotificationSenderPort> senders;

    public NotificationSenderResolver(List<NotificationSenderPort> senders) {
        this.senders = senders;
    }

    public NotificationSenderPort resolve(NotificationChannel channel) {
        return senders.stream()
                .filter(sender -> sender.supportedChannel() == channel)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un adaptador configurado para el canal " + channel
                ));
    }
}
