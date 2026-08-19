package com.plazoleta.notification_service.infrastructure.output.vonage;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.port.out.VonageSenderPort;
import com.plazoleta.notification_service.infrastructure.util.Utils;
import com.vonage.client.VonageClient;
import com.vonage.client.messages.MessageResponse;
import com.vonage.client.messages.sms.SmsTextRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class VonageSenderAdapter implements VonageSenderPort {

    private final VonageClient vonageClient;

    @Override
    public Mono<Void> send(NotificationData notificationData) {

        String phone = Utils.normalizePhone(notificationData.phone());
        String message = String.format("Recoge tu pedido con el PIN %s", notificationData.pin());

        return Mono.fromCallable(() -> {
                    MessageResponse response = vonageClient.getMessagesClient()
                            .sendMessage(
                                    SmsTextRequest.builder()
                                            .from("MiApp")
                                            .to(phone)
                                            .text(message)
                                            .build()
                            );
                    log.info("SMS enviado correctamente. message_uuid: {}, destino: {}", response.getMessageUuid(), phone);

                    return response;

                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error ->
                        log.error("Error enviando SMS a {}: {}", phone, error.getMessage(), error)
                )
                .onErrorMap(error ->
                        new DomainException(
                                DomainErrorCode.EXTERNAL_SERVICE_ERROR,
                                DomainErrorMessages.NOTIFICATION_SEND_ERROR
                        )
                )
                .then();
    }
}
