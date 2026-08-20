package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.service.NotificationApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationApplicationService notificationApplicationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void shouldSendNotificationSuccessfully() {
        String authorizationHeader = "Bearer valid-token";

        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        NotificationResponse response = NotificationResponse.builder()
                .phoneNumber("+573001234567")
                .message("Notificación enviada correctamente")
                .build();

        when(notificationApplicationService.sendNotification(anyString(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(notificationController.sendNotification(authorizationHeader, request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenAuthorizationHeaderIsInvalid() {
        String authorizationHeader = "Basic invalid-token";
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        assertThrows(
                IllegalArgumentException.class,
                () -> notificationController.sendNotification(authorizationHeader, request)
        );
    }

    @Test
    void shouldReturnErrorWhenAuthorizationHeaderIsNull() {
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        assertThrows(
                IllegalArgumentException.class,
                () -> notificationController.sendNotification(null, request)
        );
    }

    @Test
    void shouldPropagateErrorFromApplicationService() {
        String authorizationHeader = "Bearer valid-token";
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        when(notificationApplicationService.sendNotification(anyString(), any()))
                .thenReturn(Mono.error(new RuntimeException("error enviando notificación")));

        StepVerifier.create(notificationController.sendNotification(authorizationHeader, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("error enviando notificación"))
                .verify();
    }
}
