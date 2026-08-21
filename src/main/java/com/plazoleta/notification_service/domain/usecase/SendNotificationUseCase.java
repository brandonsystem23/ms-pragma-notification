package com.plazoleta.notification_service.domain.usecase;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
import com.plazoleta.notification_service.domain.model.Notification;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.api.INotificationServicePort;
import com.plazoleta.notification_service.domain.service.DomainNotificationValidator;
import com.plazoleta.notification_service.domain.service.PinGenerator;
import com.plazoleta.notification_service.domain.spi.INotificationCachePort;
import com.plazoleta.notification_service.domain.spi.INotificationSenderPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SendNotificationUseCase implements INotificationServicePort {

    private static final String EMPLOYEE_ROLE = "EMPLEADO";
    private static final String SUCCESS_MESSAGE = "Notificación enviada correctamente";

    private final INotificationCachePort iNotificationCachePort;
    private final INotificationSenderPort iNotificationSenderPort;
    private final PinGenerator pinGenerator;
    private final DomainNotificationValidator domainNotificationValidator;

    @Override
    public Mono<Notification> send(String token, String phoneNumber) {
        return Mono.fromRunnable(() -> domainNotificationValidator.validatePhone(phoneNumber))
                .then(Mono.defer(() -> iNotificationCachePort.findByToken(token)))
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.INVALID_TOKEN,
                        DomainErrorMessages.TOKEN_INVALID
                )))
                .flatMap(session -> validateEmployeeRole(session)
                        .then(Mono.defer(() -> generateStoreAndSend(phoneNumber, session.numberDocument()))));
    }

    private Mono<Void> validateEmployeeRole(AuthSession session) {
        if (!EMPLOYEE_ROLE.equals(session.role())) {
            return Mono.error(new DomainException(
                    DomainErrorCode.ACCESS_DENIED,
                    DomainErrorMessages.ROLE_NOT_ALLOWED
            ));
        }
        return Mono.empty();
    }

    private Mono<Notification> generateStoreAndSend(String phoneNumber, String numberDocument) {

        String pin = pinGenerator.generate();
        NotificationData notificationData = NotificationData.builder()
                .phoneNumber(phoneNumber)
                .pin(pin)
                .build();

        return iNotificationSenderPort.send(notificationData)
                .then(Mono.defer(() -> iNotificationCachePort.save(numberDocument, pin, notificationData)))
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.STORAGE_ERROR,
                        DomainErrorMessages.PIN_STORAGE_ERROR
                )))
                .thenReturn(Notification.builder()
                        .message(SUCCESS_MESSAGE)
                        .phoneNumber(phoneNumber)
                        .build());
    }
}
