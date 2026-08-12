package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.domain.exception.InvalidTokenException;
import com.plazoleta.notification_service.domain.exception.PinStorageException;
import com.plazoleta.notification_service.domain.exception.UnauthorizedRoleException;
import com.plazoleta.notification_service.domain.model.Notification;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.port.in.SendNotificationUseCase;
import com.plazoleta.notification_service.domain.port.out.RedisPort;
import com.plazoleta.notification_service.domain.port.out.VonageSenderPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SendNotificationService implements SendNotificationUseCase {

    private static final String EMPLOYEE_ROLE = "EMPLEADO";

    private final RedisPort redisPort;
    private final VonageSenderPort vonageSenderPort;
    private final PinGenerator pinGenerator;


    @Override
    public Mono<Notification> send(String token, String phone) {

        return redisPort.findByToken(token)
                .switchIfEmpty(Mono.error(new InvalidTokenException()))
                .flatMap(session -> validateEmployeeRole(session)
                        .then(Mono.defer(() -> generateStoreAndSend(phone, session.numberDocument()))));
    }

    private Mono<Void> validateEmployeeRole(AuthSession session) {
        if (!EMPLOYEE_ROLE.equals(session.role())) {
            return Mono.error(new UnauthorizedRoleException());
        }
        return Mono.empty();
    }

    private Mono<Notification> generateStoreAndSend(
            String phone,
            String numberDocument) {

        String pin = pinGenerator.generate();

        NotificationData notificationData = NotificationData.builder()
                .phone(phone)
                .pin(pin)
                .build();

        return vonageSenderPort.send(notificationData)
                .then(Mono.defer(() -> redisPort.save(numberDocument, pin, notificationData)))
                .switchIfEmpty(Mono.error(new PinStorageException()))
                .thenReturn(Notification.builder()
                        .message("Notificación enviada correctamente")
                        .phone(phone)
                        .build());
    }
}
