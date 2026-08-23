package com.plazoleta.notification_service.domain.usecase;

import com.plazoleta.notification_service.domain.api.INotificationServicePort;
import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
import com.plazoleta.notification_service.domain.model.Notification;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.validation.DomainNotificationValidator;
import com.plazoleta.notification_service.domain.validation.PinGenerator;
import com.plazoleta.notification_service.domain.validation.SendNotificationValidator;
import com.plazoleta.notification_service.domain.spi.INotificationCachePort;
import com.plazoleta.notification_service.domain.spi.INotificationSenderPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SendNotificationUseCase implements INotificationServicePort {

    private static final String SUCCESS_MESSAGE = "Notificación enviada correctamente";

    private final INotificationCachePort iNotificationCachePort;
    private final INotificationSenderPort iNotificationSenderPort;
    private final PinGenerator pinGenerator;
    private final DomainNotificationValidator domainNotificationValidator;
    private final SendNotificationValidator sendNotificationValidator;

    @Override
    public Mono<Notification> send(String token, String phoneNumber) {
        return Mono.defer(() -> {

                    domainNotificationValidator.validatePhone(phoneNumber);

                    return sendNotificationValidator.validate(token)
                            .flatMap(session -> generateStoreAndSend(phoneNumber, session.numberDocument()));
                });

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
