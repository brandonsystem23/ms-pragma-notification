package com.plazoleta.notification_service.application.service;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.mapper.NotificationDtoMapper;
import com.plazoleta.notification_service.domain.model.Notification;
import com.plazoleta.notification_service.domain.port.in.SendNotificationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationApplicationServiceTest {

    @Mock
    private SendNotificationUseCase sendNotificationUseCase;

    @Mock
    private NotificationDtoMapper notificationDtoMapper;

    private NotificationApplicationService notificationApplicationService;

    @BeforeEach
    void setUp() {
        notificationApplicationService = new NotificationApplicationService(
                sendNotificationUseCase,
                notificationDtoMapper
        );
    }

    @Test
    void shouldSendNotificationAndMapResponse() {
        String token = "valid-token";
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        Notification notification = Notification.builder()
                .phone("+573001234567")
                .message("Notificación enviada correctamente")
                .build();

        NotificationResponse response = NotificationResponse.builder()
                .phone("+573001234567")
                .message("Notificación enviada correctamente")
                .build();

        when(sendNotificationUseCase.send(token, request.phone())).thenReturn(Mono.just(notification));
        when(notificationDtoMapper.toResponse(notification)).thenReturn(response);

        Mono<NotificationResponse> result = notificationApplicationService.sendNotification(token, request);

        StepVerifier.create(result)
                .assertNext(actual -> {
                    assertEquals(response.phone(), actual.phone());
                    assertEquals(response.message(), actual.message());
                })
                .verifyComplete();

        verify(sendNotificationUseCase).send(token, request.phone());
        verify(notificationDtoMapper).toResponse(notification);
    }

    @Test
    void shouldPropagateErrorFromUseCase() {
        String token = "invalid-token";
        SendNotificationRequest request = new SendNotificationRequest("+573001234567");

        when(sendNotificationUseCase.send(token, request.phone()))
                .thenReturn(Mono.error(new RuntimeException("error")));

        Mono<NotificationResponse> result = notificationApplicationService.sendNotification(token, request);

        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("error"))
                .verify();

        verify(notificationDtoMapper, never()).toResponse(any());
    }
}
