package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.domain.exception.InvalidTokenException;
import com.plazoleta.notification_service.domain.exception.PinStorageException;
import com.plazoleta.notification_service.domain.exception.UnauthorizedRoleException;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.port.out.RedisPort;
import com.plazoleta.notification_service.infrastructure.output.vonage.VonageSenderAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private SendNotificationService sendNotificationService;


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

        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderAdapter.send(any())).thenReturn(Mono.empty());
        when(redisPort.save(anyString(), anyString(), any())).thenReturn(Mono.just(pin));

        StepVerifier.create(sendNotificationService.send(token, phone))
                .assertNext(notification -> {
                    assertEquals(phone, notification.phone());
                    assertEquals("Notificación enviada correctamente", notification.message());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenTokenIsInvalid() {
        String token = "invalid-token";
        String phone = "+573001234567";

        when(redisPort.findByToken(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(sendNotificationService.send(token, phone))
                .expectError(InvalidTokenException.class)
                .verify();

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

        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));

        StepVerifier.create(sendNotificationService.send(token, phone))
                .expectError(UnauthorizedRoleException.class)
                .verify();

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

        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderAdapter.send(any())).thenReturn(Mono.empty());
        when(redisPort.save(anyString(), anyString(), any())).thenReturn(Mono.empty());

        StepVerifier.create(sendNotificationService.send(token, phone))
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

        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderAdapter.send(any()))
                .thenReturn(Mono.error(new IllegalStateException("No fue posible enviar la notificación")));

        StepVerifier.create(sendNotificationService.send(token, phone))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("No fue posible enviar la notificación"))
                .verify();


    }
}
