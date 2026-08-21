package com.plazoleta.notification_service.application.handler.impl;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.mapper.NotificationDtoMapper;
import com.plazoleta.notification_service.domain.model.Notification;
import com.plazoleta.notification_service.domain.api.INotificationServicePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

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
        String token = "valid-token";
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

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

        StepVerifier.create(notificationApplicationService.sendNotification(token, request))
                .assertNext(actual -> {
                    Assertions.assertEquals(response.phoneNumber(), actual.phoneNumber());
                    Assertions.assertEquals(response.message(), actual.message());
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorFromUseCase() {
        String token = "invalid-token";
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        when(iNotificationServicePort.send(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("error")));

        StepVerifier.create(notificationApplicationService.sendNotification(token, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("error"))
                .verify();
    }
}
