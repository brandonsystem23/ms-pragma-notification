package com.plazoleta.notification_service.application.service;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.domain.port.in.SendNotificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationApplicationService {

    private final SendNotificationUseCase sendNotificationUseCase;

    public Mono<NotificationResponse> sendNotification(
            String token,
            SendNotificationRequest request
    ) {
        return sendNotificationUseCase.send(
                token,
                request.type(),
                request.phone()
        );
    }
}
