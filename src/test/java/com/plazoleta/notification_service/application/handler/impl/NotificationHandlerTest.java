package com.plazoleta.notification_service.application.handler.impl;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.mapper.NotificationDtoMapper;
import com.plazoleta.notification_service.domain.api.INotificationServicePort;
import com.plazoleta.notification_service.domain.model.Notification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationHandlerTest {

    @Mock
    private INotificationServicePort iNotificationServicePort;

    @Mock
    private NotificationDtoMapper notificationDtoMapper;

    @InjectMocks
    private NotificationHandler notificationApplicationService;

    @Test
    void shouldSendNotificationAndMapResponse() {
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");
        String numberDocument = "987654320";

        Notification notification = Notification.builder()
                .phoneNumber("+573001234567")
                .message("Notificación enviada correctamente")
                .build();

        NotificationResponse response = NotificationResponse.builder()
                .phoneNumber("+573001234567")
                .message("Notificación enviada correctamente")
                .build();

        when(iNotificationServicePort.send(anyString(), anyString()))
                .thenReturn(Mono.just(notification));

        when(notificationDtoMapper.toResponse(any()))
                .thenReturn(response);

        StepVerifier.create(notificationApplicationService.sendNotification(request, numberDocument))
                .assertNext(actual -> {
                    Assertions.assertEquals(response.phoneNumber(), actual.phoneNumber());
                    Assertions.assertEquals(response.message(), actual.message());
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorFromUseCase() {
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");
        String numberDocument = "987654320";

        when(iNotificationServicePort.send(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("error")));

        StepVerifier.create(notificationApplicationService.sendNotification(request, numberDocument))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("error"))
                .verify();
    }
}
