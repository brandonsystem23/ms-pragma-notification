package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.domain.exception.InvalidTokenException;
import com.plazoleta.notification_service.domain.exception.PinStorageException;
import com.plazoleta.notification_service.domain.exception.UnauthorizedRoleException;
import com.plazoleta.notification_service.domain.model.NotificationChannel;
import com.plazoleta.notification_service.domain.model.NotificationMessage;
import com.plazoleta.notification_service.domain.model.auth.AuthSession;
import com.plazoleta.notification_service.domain.port.in.SendNotificationUseCase;
import com.plazoleta.notification_service.domain.port.out.AuthSessionPort;
import com.plazoleta.notification_service.domain.port.out.NotificationSenderPort;
import com.plazoleta.notification_service.domain.port.out.PinRepositoryPort;
import com.plazoleta.notification_service.infrastructure.output.notification.NotificationSenderResolver;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SendNotificationService implements SendNotificationUseCase {

    private static final String EMPLOYEE_ROLE = "EMPLEADO";

    private final AuthSessionPort authSessionPort;
    private final PinRepositoryPort pinRepositoryPort;
    private final NotificationSenderResolver notificationSenderResolver;
    private final PinGenerator pinGenerator;


    @Override
    public Mono<NotificationResponse> send(String token, Integer type, String phone) {
        NotificationChannel channel = NotificationChannel.fromType(type);

        return authSessionPort.findByToken(token)
                .switchIfEmpty(Mono.error(new InvalidTokenException()))
                .flatMap(session -> validateEmployeeRole(session)
                        .then(generateStoreAndSend(channel, phone)));
    }

    private Mono<Void> validateEmployeeRole(AuthSession session) {
        if (!EMPLOYEE_ROLE.equals(session.role())) {
            return Mono.error(new UnauthorizedRoleException());
        }
        return Mono.empty();
    }

    private Mono<NotificationResponse> generateStoreAndSend(
            NotificationChannel channel,
            String phone
    ) {
        String pin = pinGenerator.generate();

        String message = "Tu pedido está listo. Tu pin de seguridad es: " + pin;

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .phone(phone)
                .message(message)
                .channel(channel)
                .build();

        NotificationSenderPort sender = notificationSenderResolver.resolve(channel);

        return pinRepositoryPort.save(phone, pin, notificationMessage)
                .switchIfEmpty(Mono.error(new PinStorageException()))
                .then(sender.send(notificationMessage))
                .thenReturn(NotificationResponse.builder()
                        .message("Notificación enviada correctamente")
                        .channel(channel.name())
                        .destination(phone)
                        .pin(pin)
                        .build());
    }
}
