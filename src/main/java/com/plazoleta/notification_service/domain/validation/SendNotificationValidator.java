package com.plazoleta.notification_service.domain.validation;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.spi.INotificationCachePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SendNotificationValidator {

    private static final String EMPLOYEE_ROLE = "EMPLEADO";

    private final INotificationCachePort notificationCachePort;

    public Mono<AuthSession> validate(String token) {
        return notificationCachePort.findByToken(token)
                .switchIfEmpty(Mono.error(new DomainException(
                        DomainErrorCode.INVALID_TOKEN,
                        DomainErrorMessages.TOKEN_INVALID
                )))
                .flatMap(this::validateRole);
    }

    private Mono<AuthSession> validateRole(AuthSession session) {
        if (!EMPLOYEE_ROLE.equals(session.role())) {
            return Mono.error(new DomainException(
                    DomainErrorCode.ACCESS_DENIED,
                    DomainErrorMessages.ROLE_NOT_ALLOWED
            ));
        }

        return Mono.just(session);
    }
}
