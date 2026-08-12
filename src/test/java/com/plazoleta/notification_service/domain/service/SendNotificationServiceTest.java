package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.domain.exception.InvalidTokenException;
import com.plazoleta.notification_service.domain.exception.PinStorageException;
import com.plazoleta.notification_service.domain.exception.UnauthorizedRoleException;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.Notification;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.port.out.RedisPort;
import com.plazoleta.notification_service.infrastructure.output.vonage.VonageSenderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendNotificationServiceTest {

    @Mock
    private RedisPort redisPort;

    @Mock
    private VonageSenderAdapter vonageSenderAdapter;

    @Mock
    private PinGenerator pinGenerator;

    private SendNotificationService sendNotificationService;

    @BeforeEach
    void setUp() {
        sendNotificationService = new SendNotificationService(
                redisPort,
                vonageSenderAdapter,
                pinGenerator
        );
    }

    @Test
    void shouldSendNotificationSuccessfully() {
        String token = "valid-token";
        String phone = "+573001234567";
        String pin = "123456";
        String document = "12345678";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument(document)
                .phone(phone)
                .email("juan@test.com")
                .build();

        when(redisPort.findByToken(token)).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderAdapter.send(any(NotificationData.class))).thenReturn(Mono.empty());
        when(redisPort.save(eq(document), eq(pin), any(NotificationData.class))).thenReturn(Mono.just(pin));

        Mono<Notification> result = sendNotificationService.send(token, phone);

        StepVerifier.create(result)
                .assertNext(notification -> {
                    assertEquals(phone, notification.phone());
                    assertEquals("Notificación enviada correctamente", notification.message());
                })
                .verifyComplete();

        ArgumentCaptor<NotificationData> captor = ArgumentCaptor.forClass(NotificationData.class);
        verify(vonageSenderAdapter).send(captor.capture());

        NotificationData sentData = captor.getValue();
        assertEquals(phone, sentData.phone());
        assertEquals(pin, sentData.pin());

        verify(redisPort).save(eq(document), eq(pin), any(NotificationData.class));
    }

    @Test
    void shouldReturnErrorWhenTokenIsInvalid() {
        String token = "invalid-token";
        String phone = "+573001234567";

        when(redisPort.findByToken(token)).thenReturn(Mono.empty());

        Mono<Notification> result = sendNotificationService.send(token, phone);

        StepVerifier.create(result)
                .expectError(InvalidTokenException.class)
                .verify();

        verify(vonageSenderAdapter, never()).send(any());
        verify(redisPort, never()).save(anyString(), anyString(), any());
    }

    @Test
    void shouldReturnErrorWhenRoleIsNotEmployee() {
        String token = "valid-token";
        String phone = "+573001234567";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("CLIENTE")
                .numberDocument("12345678")
                .phone(phone)
                .email("juan@test.com")
                .build();

        when(redisPort.findByToken(token)).thenReturn(Mono.just(session));

        Mono<Notification> result = sendNotificationService.send(token, phone);

        StepVerifier.create(result)
                .expectError(UnauthorizedRoleException.class)
                .verify();

        verify(vonageSenderAdapter, never()).send(any());
        verify(redisPort, never()).save(anyString(), anyString(), any());
    }

    @Test
    void shouldReturnErrorWhenPinStorageReturnsEmpty() {
        String token = "valid-token";
        String phone = "+573001234567";
        String pin = "123456";
        String document = "12345678";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument(document)
                .phone(phone)
                .email("juan@test.com")
                .build();

        when(redisPort.findByToken(token)).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderAdapter.send(any(NotificationData.class))).thenReturn(Mono.empty());
        when(redisPort.save(eq(document), eq(pin), any(NotificationData.class))).thenReturn(Mono.empty());

        Mono<Notification> result = sendNotificationService.send(token, phone);

        StepVerifier.create(result)
                .expectError(PinStorageException.class)
                .verify();
    }

    @Test
    void shouldPropagateErrorWhenVonageFails() {
        String token = "valid-token";
        String phone = "+573001234567";
        String pin = "123456";
        String document = "12345678";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument(document)
                .phone(phone)
                .email("juan@test.com")
                .build();

        when(redisPort.findByToken(token)).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderAdapter.send(any(NotificationData.class)))
                .thenReturn(Mono.error(new IllegalStateException("No fue posible enviar la notificación")));

        Mono<Notification> result = sendNotificationService.send(token, phone);

        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("No fue posible enviar la notificación"))
                .verify();

        verify(redisPort, never()).save(anyString(), anyString(), any());
    }
}
