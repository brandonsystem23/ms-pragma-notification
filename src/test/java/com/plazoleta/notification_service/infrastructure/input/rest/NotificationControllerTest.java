package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.service.NotificationApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationControllerTest {

    private NotificationApplicationService notificationApplicationService;
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationApplicationService = mock(NotificationApplicationService.class);
        notificationController = new NotificationController(notificationApplicationService);
    }

    @Test
    void shouldSendNotificationSuccessfully() {
        String authorizationHeader = "Bearer valid-token";

        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        NotificationResponse response = NotificationResponse.builder()
                .phone("+573001234567")
                .message("Notificación enviada correctamente")
                .build();

        when(notificationApplicationService.sendNotification("valid-token", request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(notificationController.sendNotification(authorizationHeader, request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenAuthorizationHeaderIsInvalid() {
        String authorizationHeader = "Basic invalid-token";
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificationController.sendNotification(authorizationHeader, request)
        );

        assertEquals("Authorization header inválido", exception.getMessage());
    }

    @Test
    void shouldReturnErrorWhenAuthorizationHeaderIsNull() {
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificationController.sendNotification(null, request)
        );

        assertEquals("Authorization header inválido", exception.getMessage());
    }

    @Test
    void shouldPropagateErrorFromApplicationService() {
        String authorizationHeader = "Bearer valid-token";

        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        when(notificationApplicationService.sendNotification("valid-token", request))
                .thenReturn(Mono.error(new RuntimeException("error enviando notificación")));

        StepVerifier.create(notificationController.sendNotification(authorizationHeader, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("error enviando notificación"))
                .verify();
    }
}
