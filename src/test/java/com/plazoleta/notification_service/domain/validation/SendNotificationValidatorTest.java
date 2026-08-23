package com.plazoleta.notification_service.domain.validation;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.spi.INotificationCachePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendNotificationValidatorTest {

    @Mock
    private INotificationCachePort notificationCachePort;

    @InjectMocks
    private SendNotificationValidator sendNotificationValidator;


    @Test
    void shouldReturnSessionWhenTokenIsValidAndRoleIsEmployee() {
        String token = "valid-token";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument("12345678")
                .phone("+573001234567")
                .email("juan@test.com")
                .build();

        when(notificationCachePort.findByToken(anyString()))
                .thenReturn(Mono.just(session));

        StepVerifier.create(sendNotificationValidator.validate(token))
                .expectNext(session)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenTokenDoesNotExist() {
        String token = "invalid-token";

        when(notificationCachePort.findByToken(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(sendNotificationValidator.validate(token))
                .expectErrorMatches(error ->
                        error instanceof DomainException
                                && ((DomainException) error).getCode() == DomainErrorCode.INVALID_TOKEN
                                && error.getMessage().equals(DomainErrorMessages.TOKEN_INVALID))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenRoleIsNotEmployee() {
        String token = "valid-token";

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("CLIENTE")
                .numberDocument("12345678")
                .phone("+573001234567")
                .email("juan@test.com")
                .build();

        when(notificationCachePort.findByToken(anyString()))
                .thenReturn(Mono.just(session));

        StepVerifier.create(sendNotificationValidator.validate(token))
                .expectErrorMatches(error ->
                        error instanceof DomainException
                                && ((DomainException) error).getCode() == DomainErrorCode.ACCESS_DENIED
                                && error.getMessage().equals(DomainErrorMessages.ROLE_NOT_ALLOWED))
                .verify();
    }
}
