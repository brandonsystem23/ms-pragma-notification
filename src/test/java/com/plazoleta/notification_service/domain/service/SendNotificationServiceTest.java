package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.port.out.RedisPort;
import com.plazoleta.notification_service.domain.port.out.VonageSenderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendNotificationServiceTest {

    @Mock
    private RedisPort redisPort;

    @Mock
    private VonageSenderPort vonageSenderPort;

    @Mock
    private PinGenerator pinGenerator;

    @Mock
    private DomainNotificationValidator domainNotificationValidator;

    private SendNotificationService sendNotificationService;

    @BeforeEach
    void setUp() {
        sendNotificationService = new SendNotificationService(
                redisPort,
                vonageSenderPort,
                pinGenerator,
                domainNotificationValidator
        );
    }

    @Test
    void shouldSendNotificationSuccessfully() {
        String token = "valid-token";
        String phoneNumber = "+573001234567";
        String pin = "123456";
        String document = "12345678";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument(document)
                .phone(phoneNumber)
                .email("juan@test.com")
                .build();

        doNothing().when(domainNotificationValidator).validatePhone(phoneNumber);
        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderPort.send(any())).thenReturn(Mono.empty());
        when(redisPort.save(anyString(), anyString(), any())).thenReturn(Mono.just(pin));

        StepVerifier.create(sendNotificationService.send(token, phoneNumber))
                .assertNext(notification -> {
                    assertEquals(phoneNumber, notification.phoneNumber());
                    assertEquals("Notificación enviada correctamente", notification.message());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnValidationErrorWhenPhoneIsInvalid() {
        String token = "valid-token";
        String phoneNumber = "ABC123";

        doThrow(new DomainException(
                DomainErrorCode.VALIDATION_ERROR,
                DomainErrorMessages.PHONE_INVALID
        )).when(domainNotificationValidator).validatePhone(phoneNumber);

        StepVerifier.create(sendNotificationService.send(token, phoneNumber))
                .expectErrorMatches(error ->
                        error instanceof DomainException
                                && ((DomainException) error).getCode() == DomainErrorCode.VALIDATION_ERROR
                                && error.getMessage().equals(DomainErrorMessages.PHONE_INVALID))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenTokenIsInvalid() {
        String token = "invalid-token";
        String phoneNumber = "+573001234567";

        doNothing().when(domainNotificationValidator).validatePhone(phoneNumber);
        when(redisPort.findByToken(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(sendNotificationService.send(token, phoneNumber))
                .expectErrorMatches(error ->
                        error instanceof DomainException
                                && ((DomainException) error).getCode() == DomainErrorCode.INVALID_TOKEN
                                && error.getMessage().equals(DomainErrorMessages.TOKEN_INVALID))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenRoleIsNotEmployee() {
        String token = "valid-token";
        String phoneNumber = "+573001234567";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("CLIENTE")
                .numberDocument("12345678")
                .phone(phoneNumber)
                .email("juan@test.com")
                .build();

        doNothing().when(domainNotificationValidator).validatePhone(phoneNumber);
        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));

        StepVerifier.create(sendNotificationService.send(token, phoneNumber))
                .expectErrorMatches(error ->
                        error instanceof DomainException
                                && ((DomainException) error).getCode() == DomainErrorCode.ACCESS_DENIED
                                && error.getMessage().equals(DomainErrorMessages.ROLE_NOT_ALLOWED))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenPinStorageReturnsEmpty() {
        String token = "valid-token";
        String phoneNumber = "+573001234567";
        String pin = "123456";
        String document = "12345678";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument(document)
                .phone(phoneNumber)
                .email("juan@test.com")
                .build();

        doNothing().when(domainNotificationValidator).validatePhone(phoneNumber);
        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderPort.send(any())).thenReturn(Mono.empty());
        when(redisPort.save(anyString(), anyString(), any())).thenReturn(Mono.empty());

        StepVerifier.create(sendNotificationService.send(token, phoneNumber))
                .expectErrorMatches(error ->
                        error instanceof DomainException
                                && ((DomainException) error).getCode() == DomainErrorCode.STORAGE_ERROR
                                && error.getMessage().equals(DomainErrorMessages.PIN_STORAGE_ERROR))
                .verify();
    }

    @Test
    void shouldPropagateErrorWhenVonageFails() {
        String token = "valid-token";
        String phoneNumber = "+573001234567";
        String pin = "123456";
        String document = "12345678";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument(document)
                .phone(phoneNumber)
                .email("juan@test.com")
                .build();

        doNothing().when(domainNotificationValidator).validatePhone(phoneNumber);
        when(redisPort.findByToken(anyString())).thenReturn(Mono.just(session));
        when(pinGenerator.generate()).thenReturn(pin);
        when(vonageSenderPort.send(any()))
                .thenReturn(Mono.error(new DomainException(
                        DomainErrorCode.EXTERNAL_SERVICE_ERROR,
                        DomainErrorMessages.NOTIFICATION_SEND_ERROR
                )));

        StepVerifier.create(sendNotificationService.send(token, phoneNumber))
                .expectErrorMatches(error ->
                        error instanceof DomainException
                                && ((DomainException) error).getCode() == DomainErrorCode.EXTERNAL_SERVICE_ERROR
                                && error.getMessage().equals(DomainErrorMessages.NOTIFICATION_SEND_ERROR))
                .verify();
    }
}
