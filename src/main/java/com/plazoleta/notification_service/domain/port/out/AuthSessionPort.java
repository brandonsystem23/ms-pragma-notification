package com.plazoleta.notification_service.domain.port.out;

import com.plazoleta.notification_service.domain.model.auth.AuthSession;
import reactor.core.publisher.Mono;

public interface AuthSessionPort {

    Mono<AuthSession> findByToken(String token);
}
