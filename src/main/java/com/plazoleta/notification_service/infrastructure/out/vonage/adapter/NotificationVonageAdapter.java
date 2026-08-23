package com.plazoleta.notification_service.infrastructure.out.vonage.adapter;

import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.spi.INotificationSenderPort;
import com.plazoleta.notification_service.infrastructure.util.Utils;
import com.vonage.client.VonageClient;
import com.vonage.client.messages.sms.SmsTextRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationVonageAdapter implements INotificationSenderPort {

    private static final String FROM = "MiApp";
    private static final String MESSAGE_TEMPLATE = "Recoge tu pedido con el PIN %s";

    private final VonageClient vonageClient;

    @Override
    public Mono<Void> send(NotificationData notificationData) {

        String phoneNumber = Utils.normalizePhone(notificationData.phoneNumber());
        String message = String.format(MESSAGE_TEMPLATE, notificationData.pin());

        return Mono.defer(() ->
                    Mono.just(vonageClient.getMessagesClient()
                            .sendMessage(SmsTextRequest.builder()
                                    .from(FROM)
                                    .to(phoneNumber)
                                    .text(message)
                                    .build()))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(messageResponse ->
                        log.info("SMS enviado correctamente. message_uuid: {}, destino: {}",
                                messageResponse.getMessageUuid(), phoneNumber))
                .doOnError(error ->
                        log.error("Error enviando SMS a {}: {}", phoneNumber, error.getMessage(), error)
                )
                .onErrorMap(error ->
                        new RuntimeException("No fue posible enviar la notificación", error)
                )
                .then();
    }
}
