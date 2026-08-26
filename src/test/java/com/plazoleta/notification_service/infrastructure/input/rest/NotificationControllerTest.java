package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.handler.INotificationHandler;
import com.plazoleta.notification_service.infrastructure.security.jwt.AuthenticatedUser;
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
class NotificationControllerTest {

    @Mock
    private INotificationHandler notificationApplicationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void shouldSendNotificationSuccessfully() {
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .userId(7L)
                .fullName("Sofia Gomez")
                .role("EMPLEADO")
                .numberDocument("987654320")
                .phone("+573004445566")
                .email("sofia.gomez@plazoleta.com")
                .build();

        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        NotificationResponse response = NotificationResponse.builder()
                .phoneNumber("+573001234567")
                .message("Notificación enviada correctamente")
                .build();

        when(notificationApplicationService.sendNotification(any(), anyString()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(notificationController.sendNotification(authenticatedUser, request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorFromApplicationService() {
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .userId(7L)
                .fullName("Sofia Gomez")
                .role("EMPLEADO")
                .numberDocument("987654320")
                .phone("+573004445566")
                .email("sofia.gomez@plazoleta.com")
                .build();

        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        when(notificationApplicationService.sendNotification(any(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("error enviando notificación")));

        StepVerifier.create(notificationController.sendNotification(authenticatedUser, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("error enviando notificación"))
                .verify();
    }
}
